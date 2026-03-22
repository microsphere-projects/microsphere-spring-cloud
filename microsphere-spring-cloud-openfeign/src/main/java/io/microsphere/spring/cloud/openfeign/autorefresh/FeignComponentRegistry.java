package io.microsphere.spring.cloud.openfeign.autorefresh;

import feign.Contract;
import feign.QueryMapEncoder;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import io.microsphere.spring.cloud.openfeign.components.CompositedRequestInterceptor;
import io.microsphere.spring.cloud.openfeign.components.Refreshable;
import org.springframework.beans.factory.BeanFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.microsphere.collection.Lists.ofList;
import static io.microsphere.collection.Maps.ofMap;
import static io.microsphere.collection.Sets.ofSet;
import static io.microsphere.constants.SymbolConstants.LEFT_SQUARE_BRACKET;
import static io.microsphere.spring.boot.context.properties.source.util.ConfigurationPropertyUtils.toDashedForm;
import static io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor.INSTANCE;
import static io.microsphere.util.Assert.assertNoNullElements;
import static io.microsphere.util.Assert.assertNotBlank;
import static io.microsphere.util.Assert.assertNotEmpty;
import static io.microsphere.util.Assert.assertNotNull;
import static io.microsphere.util.StringUtils.isBlank;
import static io.microsphere.util.StringUtils.substringBefore;

/**
 * Feign Component Registry
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class FeignComponentRegistry {

    private static final Map<String, Class<?>> configComponentMappings = ofMap(
            "retryer", Retryer.class,
            "error-decoder", ErrorDecoder.class,
            "request-interceptors", RequestInterceptor.class,
            "default-request-headers", RequestInterceptor.class,
            "default-query-parameters", RequestInterceptor.class,
            "decoder", Decoder.class,
            "encoder", Encoder.class,
            "contract", Contract.class,
            "query-map-encoder", QueryMapEncoder.class
    );

    private final Map<String, List<Refreshable>> refreshableComponents = new ConcurrentHashMap<>(32);

    private final Map<String, CompositedRequestInterceptor> interceptorsMap = new ConcurrentHashMap<>(32);

    private final String defaultClientName;

    private final BeanFactory beanFactory;

    protected static Class<?> getComponentClass(String config) {
        if (isBlank(config)) {
            return null;
        }
        String normalizedConfig = normalizeConfig(config);
        return configComponentMappings.get(normalizedConfig);
    }

    static String normalizeConfig(String config) {
        String normalizedConfig = substringBefore(config, LEFT_SQUARE_BRACKET);
        return toDashedForm(normalizedConfig);
    }

    /**
     * Constructs a new {@link FeignComponentRegistry} with the specified default client name
     * and {@link BeanFactory}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * FeignComponentRegistry registry = new FeignComponentRegistry("default", beanFactory);
     * }</pre>
     *
     * @param defaultClientName the name of the default Feign client configuration
     * @param beanFactory the {@link BeanFactory} used to resolve beans
     */
    public FeignComponentRegistry(String defaultClientName, BeanFactory beanFactory) {
        this.defaultClientName = defaultClientName;
        this.beanFactory = beanFactory;
    }

    /**
     * Registers a list of {@link Refreshable} components for the specified Feign client name.
     * Components are appended to any previously registered components for that client.
     *
     * <p>Example Usage:
     * <pre>{@code
     * registry.register("my-client", List.of(decoratedDecoder, decoratedEncoder));
     * }</pre>
     *
     * @param clientName the Feign client name to associate the components with
     * @param components the list of {@link Refreshable} components to register
     */
    public void register(String clientName, List<Refreshable> components) {
        assertNotBlank(clientName, () -> "The 'clientName' must not be blank!");
        assertNotEmpty(components, () -> "The 'components' must not be empty!");
        assertNoNullElements(components, () -> "The 'components' must not contain the null  element!");
        List<Refreshable> componentList = this.refreshableComponents.computeIfAbsent(clientName, name -> new ArrayList<>());
        componentList.addAll(components);
    }

    /**
     * Registers a single {@link Refreshable} component for the specified Feign client name.
     *
     * <p>Example Usage:
     * <pre>{@code
     * registry.register("my-client", decoratedContract);
     * }</pre>
     *
     * @param clientName the Feign client name to associate the component with
     * @param component the {@link Refreshable} component to register
     */
    public void register(String clientName, Refreshable component) {
        register(clientName, ofList(component));
    }

    /**
     * Registers a {@link RequestInterceptor} for the specified Feign client name. Interceptors
     * are grouped into a {@link CompositedRequestInterceptor} per client. Returns the composited
     * interceptor if this is the first interceptor for the client, otherwise returns a no-op instance.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RequestInterceptor result = registry.registerRequestInterceptor("my-client",
     *     template -> template.header("Authorization", "Bearer token"));
     * }</pre>
     *
     * @param clientName the Feign client name
     * @param requestInterceptor the {@link RequestInterceptor} to register
     * @return the {@link CompositedRequestInterceptor} if first registration, or a no-op interceptor
     */
    public RequestInterceptor registerRequestInterceptor(String clientName, RequestInterceptor requestInterceptor) {
        assertNotBlank(clientName, () -> "The 'clientName' must not be blank!");
        assertNotNull(requestInterceptor, () -> "The 'requestInterceptor' must not be null!");
        CompositedRequestInterceptor compositedRequestInterceptor = this.interceptorsMap.computeIfAbsent(clientName, (name) -> new CompositedRequestInterceptor(clientName, beanFactory));
        if (compositedRequestInterceptor.addRequestInterceptor(requestInterceptor)) {
            return compositedRequestInterceptor;
        }
        return INSTANCE;
    }


    /**
     * Refreshes all registered components for the specified Feign client whose component
     * types match the given changed configuration keys.
     *
     * <p>Example Usage:
     * <pre>{@code
     * registry.refresh("my-client", "decoder", "encoder");
     * }</pre>
     *
     * @param clientName the Feign client name whose components should be refreshed
     * @param changedConfigs the configuration keys that have changed
     */
    public void refresh(String clientName, String... changedConfigs) {
        refresh(clientName, ofSet(changedConfigs));
    }

    public synchronized void refresh(String clientName, Set<String> changedConfigs) {
        Set<Class<?>> effectiveComponents = new HashSet<>(changedConfigs.size());

        boolean hasInterceptor = false;
        for (String changedConfig : changedConfigs) {
            changedConfig = changedConfig.replace(clientName + ".", "");
            Class<?> clazz = getComponentClass(changedConfig);
            if (clazz != null) {
                effectiveComponents.add(clazz);
                hasInterceptor = RequestInterceptor.class.equals(clazz);
            }
        }

        if (defaultClientName.equals(clientName)) {
            //default configs changed, need refresh all
            refreshableComponents.values().stream()
                    .flatMap(List::stream)
                    .filter(component -> isComponentPresent(component, effectiveComponents))
                    .forEach(Refreshable::refresh);
            if (hasInterceptor) {
                this.interceptorsMap.values().forEach(CompositedRequestInterceptor::refresh);
            }
            return;
        }

        List<Refreshable> components = this.refreshableComponents.get(clientName);
        if (components != null) {
            components.stream()
                    .filter(component -> isComponentPresent(component, effectiveComponents))
                    .forEach(Refreshable::refresh);
        }

        if (hasInterceptor) {
            CompositedRequestInterceptor requestInterceptor = this.interceptorsMap.get(clientName);
            if (requestInterceptor != null)
                requestInterceptor.refresh();
        }
    }

    static boolean isComponentPresent(Refreshable component, Iterable<Class<?>> effectiveComponents) {
        return isComponentClassPresent(component.getClass(), effectiveComponents);
    }

    static boolean isComponentClassPresent(Class<?> componentsClass, Iterable<Class<?>> effectiveComponents) {
        for (Class<?> actualComponent : effectiveComponents) {
            if (actualComponent.isAssignableFrom(componentsClass)) {
                return true;
            }
        }
        return false;
    }
}
