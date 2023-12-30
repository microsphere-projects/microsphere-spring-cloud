package io.microsphere.spring.cloud.openfeign.components;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class CompositedRequestInterceptor implements RequestInterceptor, Refreshable {

    private final BeanFactory beanFactory;
    private final String contextId;

    private final Set<RequestInterceptor> set = new HashSet<>();

    public CompositedRequestInterceptor(String contextId, BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.contextId = contextId;
    }


    @Override
    public void apply(RequestTemplate template) {
        synchronized (this.set) {
            if (!this.set.isEmpty())
                set.forEach(requestInterceptor -> requestInterceptor.apply(template));
        }

    }

    public boolean addRequestInterceptor(RequestInterceptor requestInterceptor) {
        synchronized (this.set) {
            boolean isFirst = this.set.isEmpty();
            this.set.add(requestInterceptor);
            return isFirst;
        }

    }

    private RequestInterceptor getInterceptorOrInstantiate(Class<? extends RequestInterceptor> clazz) {
        try {
            return this.beanFactory.getBean(clazz);
        } catch (Exception e) {
            return BeanUtils.instantiateClass(clazz);
        }
    }

    @Override
    public void refresh() {
        FeignClientProperties properties = this.beanFactory.getBean(FeignClientProperties.class);
        Set<Class<RequestInterceptor>> interceptors = new HashSet<>();
        //headers
        Map<String, Collection<String>> headers = new HashMap<>();
        Map<String, Collection<String>> params = new HashMap<>();
        if (properties != null) {
            FeignClientProperties.FeignClientConfiguration defaultConfiguration = properties.getConfig().get(properties.getDefaultConfig());
            FeignClientProperties.FeignClientConfiguration current = properties.getConfig().get(contextId);
            if (defaultConfiguration != null && defaultConfiguration.getRequestInterceptors() != null)
                interceptors.addAll(defaultConfiguration.getRequestInterceptors());
            if (current != null && current.getRequestInterceptors() != null)
                interceptors.addAll(current.getRequestInterceptors());

            if (defaultConfiguration != null && defaultConfiguration.getDefaultRequestHeaders() != null)
                headers.putAll(defaultConfiguration.getDefaultRequestHeaders());

            if (current != null && current.getDefaultRequestHeaders() != null) {
                current.getDefaultRequestHeaders().forEach(headers::putIfAbsent);
            }

            if (defaultConfiguration != null && defaultConfiguration.getDefaultQueryParameters() != null)
                params.putAll(defaultConfiguration.getDefaultRequestHeaders());

            if (current != null && current.getDefaultQueryParameters() != null) {
                current.getDefaultQueryParameters().forEach(params::putIfAbsent);
            }

        }

        synchronized (this.set) {
            this.set.clear();
            for (Class<RequestInterceptor> interceptorClass : interceptors)
                set.add(getInterceptorOrInstantiate(interceptorClass));

            if (!CollectionUtils.isEmpty(headers))
                set.add(requestTemplate -> {
                    Map<String, Collection<String>> requestHeader = requestTemplate.headers();
                    headers.keySet().forEach(key -> {
                        if (!requestHeader.containsKey(key)) {
                            requestTemplate.header(key, headers.get(key));
                        }
                    });
                });

            if (!CollectionUtils.isEmpty(params))
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
}
