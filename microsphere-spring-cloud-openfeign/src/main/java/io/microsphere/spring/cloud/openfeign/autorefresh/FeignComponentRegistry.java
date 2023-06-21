package io.microsphere.spring.cloud.openfeign.autorefresh;

import feign.RequestInterceptor;
import io.microsphere.spring.cloud.openfeign.components.CompositedRequestInterceptor;
import io.microsphere.spring.cloud.openfeign.components.Refreshable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class FeignComponentRegistry {

    private final Map<String, List<Refreshable>> refreshableComponents = new ConcurrentHashMap<>(32);
    private final Map<String, CompositedRequestInterceptor> interceptorMap = new ConcurrentHashMap<>(32);

    private final String DEFAULT_CLIENT_NAME;
    private final BeanFactory beanFactory;

    public FeignComponentRegistry(String defaultClientName, BeanFactory beanFactory) {
        this.DEFAULT_CLIENT_NAME = defaultClientName;
        this.beanFactory = beanFactory;
    }

    public void register(String clientName, List<Refreshable> components) {
        List<Refreshable> componentList = this.refreshableComponents.computeIfAbsent(clientName, name -> new ArrayList<>());
        componentList.addAll(componentList);
    }

    public void register(String clientName, Refreshable component) {
        List<Refreshable> componentList = this.refreshableComponents.computeIfAbsent(clientName, name -> new ArrayList<>());
        componentList.add(component);
    }

    public boolean registerRequestInterceptor(String clientName, RequestInterceptor requestInterceptor) {
        CompositedRequestInterceptor compositedRequestInterceptor = this.interceptorMap.computeIfAbsent(clientName, (name) -> new CompositedRequestInterceptor(clientName, beanFactory));
        return compositedRequestInterceptor.addRequestInterceptor(requestInterceptor);
    }

    public CompositedRequestInterceptor getRequestInterceptor(String clientName) {
        return this.interceptorMap.get(clientName);
    }


    public synchronized void refresh(String clientName) {
        if (DEFAULT_CLIENT_NAME.equals(clientName)) {
            //default configs changed, need refresh all
            refreshableComponents.values().stream()
                    .flatMap(List::stream)
                    .forEach(Refreshable::refresh);
            this.interceptorMap.values()
                    .forEach(CompositedRequestInterceptor::refresh);
            return;
        }
        List<Refreshable> components = this.refreshableComponents.get(clientName);
        if (components != null)
            components.forEach(Refreshable::refresh);

        CompositedRequestInterceptor requestInterceptor = this.interceptorMap.get(clientName);
        if (requestInterceptor != null)
            requestInterceptor.refresh();
    }

}
