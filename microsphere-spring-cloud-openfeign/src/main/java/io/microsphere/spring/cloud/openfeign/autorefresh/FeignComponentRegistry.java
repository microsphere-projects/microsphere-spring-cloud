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

    public FeignComponentRegistry(String defaultClientName, BeanFactory beanFactory) {
        this.defaultClientName = defaultClientName;
        this.beanFactory = beanFactory;
    }

    public void register(String clientName, List<Refreshable> components) {
        assertNotBlank(clientName, () -> "The 'clientName' must not be blank!");
        assertNotEmpty(components, () -> "The 'components' must not be empty!");
        assertNoNullElements(components, () -> "The 'components' must not contain the null  element!");
        List<Refreshable> componentList = this.refreshableComponents.computeIfAbsent(clientName, name -> new ArrayList<>());
        componentList.addAll(components);
    }

    public void register(String clientName, Refreshable component) {
        register(clientName, ofList(component));
    }

    public RequestInterceptor registerRequestInterceptor(String clientName, RequestInterceptor requestInterceptor) {
        assertNotBlank(clientName, () -> "The 'clientName' must not be blank!");
        assertNotNull(requestInterceptor, () -> "The 'requestInterceptor' must not be null!");
        CompositedRequestInterceptor compositedRequestInterceptor = this.interceptorsMap.computeIfAbsent(clientName, (name) -> new CompositedRequestInterceptor(clientName, beanFactory));
        if (compositedRequestInterceptor.addRequestInterceptor(requestInterceptor)) {
            return compositedRequestInterceptor;
        }
        return INSTANCE;
    }


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
