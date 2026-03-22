package io.microsphere.spring.cloud.openfeign.components;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static io.microsphere.collection.CollectionUtils.isNotEmpty;
import static io.microsphere.collection.MapUtils.isNotEmpty;
import static java.util.Collections.unmodifiableSet;
import static org.springframework.beans.BeanUtils.instantiateClass;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class CompositedRequestInterceptor implements RequestInterceptor, Refreshable {

    private final BeanFactory beanFactory;

    private final String contextId;

    private final Set<RequestInterceptor> set = new LinkedHashSet<>();

    /**
     * Constructs a new {@link CompositedRequestInterceptor} for the given Feign client context.
     *
     * <p>Example Usage:
     * <pre>{@code
     * CompositedRequestInterceptor interceptor =
     *     new CompositedRequestInterceptor("my-client", beanFactory);
     * }</pre>
     *
     * @param contextId the Feign client context identifier
     * @param beanFactory the {@link BeanFactory} used to resolve interceptor beans during refresh
     */
    public CompositedRequestInterceptor(String contextId, BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.contextId = contextId;
    }

    /**
     * Returns an unmodifiable view of the currently registered {@link RequestInterceptor} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Set<RequestInterceptor> interceptors = compositedInterceptor.getRequestInterceptors();
     * }</pre>
     *
     * @return an unmodifiable {@link java.util.Set} of request interceptors
     */
    public Set<RequestInterceptor> getRequestInterceptors() {
        return unmodifiableSet(set);
    }

    /**
     * Applies all registered {@link RequestInterceptor} instances to the given
     * {@link RequestTemplate} in order. This method is thread-safe.
     *
     * <p>Example Usage:
     * <pre>{@code
     * compositedInterceptor.apply(requestTemplate);
     * }</pre>
     *
     * @param template the {@link RequestTemplate} to apply interceptors to
     */
    @Override
    public void apply(RequestTemplate template) {
        synchronized (this.set) {
            set.forEach(requestInterceptor -> requestInterceptor.apply(template));
        }
    }

    /**
     * Adds a {@link RequestInterceptor} to this composite. Returns {@code true} if this
     * is the first interceptor added (i.e., the set was previously empty), {@code false} otherwise.
     * This method is thread-safe.
     *
     * <p>Example Usage:
     * <pre>{@code
     * boolean isFirst = compositedInterceptor.addRequestInterceptor(myInterceptor);
     * }</pre>
     *
     * @param requestInterceptor the {@link RequestInterceptor} to add
     * @return {@code true} if this was the first interceptor added, {@code false} otherwise
     */
    public boolean addRequestInterceptor(RequestInterceptor requestInterceptor) {
        synchronized (this.set) {
            boolean isFirst = this.set.isEmpty();
            this.set.add(requestInterceptor);
            return isFirst;
        }
    }

    private RequestInterceptor getInterceptorOrInstantiate(Class<? extends RequestInterceptor> clazz) {
        return getOrInstantiate(clazz);
    }

    /**
     * Refreshes the set of {@link RequestInterceptor} instances by reloading them from
     * the current {@link FeignClientProperties} configuration. This includes request
     * interceptors, default request headers, and default query parameters.
     *
     * <p>Example Usage:
     * <pre>{@code
     * compositedInterceptor.refresh();
     * }</pre>
     */
    @Override
    public void refresh() {
        FeignClientProperties properties = getOrInstantiate(FeignClientProperties.class);
        Set<Class<RequestInterceptor>> interceptors = new HashSet<>();
        //headers
        Map<String, Collection<String>> headers = new HashMap<>();
        Map<String, Collection<String>> params = new HashMap<>();

        Map<String, FeignClientConfiguration> config = properties.getConfig();
        FeignClientConfiguration defaultConfiguration = config.get(properties.getDefaultConfig());
        FeignClientConfiguration currentConfiguration = config.get(contextId);

        addAll(defaultConfiguration::getRequestInterceptors, interceptors);
        addAll(currentConfiguration::getRequestInterceptors, interceptors);

        putIfAbsent(defaultConfiguration::getDefaultRequestHeaders, headers);
        putIfAbsent(currentConfiguration::getDefaultRequestHeaders, headers);

        putIfAbsent(defaultConfiguration::getDefaultQueryParameters, params);
        putIfAbsent(currentConfiguration::getDefaultQueryParameters, params);

        synchronized (this.set) {
            this.set.clear();
            for (Class<RequestInterceptor> interceptorClass : interceptors) {
                set.add(getInterceptorOrInstantiate(interceptorClass));
            }

            if (isNotEmpty(headers))
                set.add(requestTemplate -> {
                    Map<String, Collection<String>> requestHeader = requestTemplate.headers();
                    headers.keySet().forEach(key -> {
                        if (!requestHeader.containsKey(key)) {
                            requestTemplate.header(key, headers.get(key));
                        }
                    });
                });

            if (isNotEmpty(params))
                set.add(requestTemplate -> {
                    Map<String, Collection<String>> requestQueries = requestTemplate.queries();
                    params.keySet().forEach(key -> {
                        if (!requestQueries.containsKey(key)) {
                            requestTemplate.query(key, params.get(key));
                        }
                    });
                });
        }
    }

    static <E> void addAll(Supplier<Collection<E>> sourceSupplier, Collection<E> target) {
        Collection<E> source = sourceSupplier.get();
        if (isNotEmpty(source)) {
            source.forEach(target::add);
        }
    }

    <T> T getOrInstantiate(Class<T> clazz) {
        ObjectProvider<T> beanProvider = this.beanFactory.getBeanProvider(clazz);
        return beanProvider.getIfAvailable(() -> instantiateClass(clazz));
    }

    static <K, V> void putIfAbsent(Supplier<Map<K, V>> sourceSupplier, Map<K, V> target) {
        Map<K, V> source = sourceSupplier.get();
        if (isNotEmpty(source)) {
            source.forEach(target::putIfAbsent);
        }
    }
}