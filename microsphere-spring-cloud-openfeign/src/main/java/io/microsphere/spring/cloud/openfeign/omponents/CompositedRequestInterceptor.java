package io.microsphere.spring.cloud.openfeign.omponents;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class CompositedRequestInterceptor implements RequestInterceptor, Refreshable {

    private final BeanFactory beanFactory;
    private String contextId;

    private List<RequestInterceptor> list = new ArrayList<>();

    public CompositedRequestInterceptor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        refresh();
    }


    @Override
    public void apply(RequestTemplate template) {
        if (!this.list.isEmpty())
            list.forEach(requestInterceptor -> requestInterceptor.apply(template));
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

        this.list.clear();
        for (Class<RequestInterceptor> interceptorClass : interceptors)
            list.add(this.beanFactory.getBean(interceptorClass));

        if (!CollectionUtils.isEmpty(headers))
            list.add(requestTemplate -> {
                Map<String, Collection<String>> requestHeader = requestTemplate.headers();
                headers.keySet().forEach(key -> {
                    if (!requestHeader.containsKey(key)) {
                        requestTemplate.header(key, headers.get(key));
                    }
                });
            });

        if (!CollectionUtils.isEmpty(params))
            list.add(requestTemplate -> {
                Map<String, Collection<String>> requestQueries = requestTemplate.queries();
                params.keySet().forEach(key -> {
                    if (!requestQueries.containsKey(key)) {
                        requestTemplate.query(key, params.get(key));
                    }
                });
            });


    }

    public void injectContextId(String contextId) {
        this.contextId = contextId;
    }
}
