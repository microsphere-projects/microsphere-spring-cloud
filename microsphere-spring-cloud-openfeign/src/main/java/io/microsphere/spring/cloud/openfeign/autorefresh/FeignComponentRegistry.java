package io.microsphere.spring.cloud.openfeign.autorefresh;

import feign.Contract;
import feign.QueryMapEncoder;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import io.microsphere.spring.cloud.openfeign.components.CompositedRequestInterceptor;
import io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor;
import io.microsphere.spring.cloud.openfeign.components.Refreshable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class FeignComponentRegistry {

    private static final Map<String, Class<?>> configComponentMappings = new HashMap<>(16);

    private final Map<String, List<Refreshable>> refreshableComponents = new ConcurrentHashMap<>(32);
    private final Map<String, CompositedRequestInterceptor> interceptorMap = new ConcurrentHashMap<>(32);

    private final String DEFAULT_CLIENT_NAME;
    private final BeanFactory beanFactory;

    static {
        configComponentMappings.put("retryer", Retryer.class);
        configComponentMappings.put("errorDecoder", ErrorDecoder.class);
        configComponentMappings.put("error-decoder", ErrorDecoder.class);
        configComponentMappings.put("requestInterceptors", RequestInterceptor.class);
        configComponentMappings.put("request-interceptors", RequestInterceptor.class);
        configComponentMappings.put("defaultRequestHeaders", RequestInterceptor.class);
        configComponentMappings.put("default-request-headers", RequestInterceptor.class);
        configComponentMappings.put("defaultQueryParameters", RequestInterceptor.class);
        configComponentMappings.put("default-query-parameters", RequestInterceptor.class);
        configComponentMappings.put("decoder", Decoder.class);
        configComponentMappings.put("encoder", Encoder.class);
        configComponentMappings.put("contract", Contract.class);
        configComponentMappings.put("queryMapEncoder", QueryMapEncoder.class);
        configComponentMappings.put("query-map-encoder", QueryMapEncoder.class);
    }

    protected static Class<?> getComponentClass(String config) {
        if (ObjectUtils.isEmpty(config))
            return null;
        //组合
        if (config.endsWith("]")) {
            for (Map.Entry<String, Class<?>> next : configComponentMappings.entrySet()) {
                if (config.startsWith(next.getKey()))
                    return next.getValue();
            }
        } else {
            for (Map.Entry<String, Class<?>> next : configComponentMappings.entrySet()) {
                if (config.equals(next.getKey()))
                    return next.getValue();
            }
        }
        return null;
    }

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

    public RequestInterceptor registerRequestInterceptor(String clientName, RequestInterceptor requestInterceptor) {
        CompositedRequestInterceptor compositedRequestInterceptor = this.interceptorMap.computeIfAbsent(clientName, (name) -> new CompositedRequestInterceptor(clientName, beanFactory));
        if (compositedRequestInterceptor.addRequestInterceptor(requestInterceptor)) {
            return compositedRequestInterceptor;
        } else return NoOpRequestInterceptor.INSTANCE;
    }


    public synchronized void refresh(String clientName, Set<String> changedConfig) {

        Set<Class<?>> effectiveComponents = new HashSet<>();
        boolean hasInterceptor = false;
        for (String value : changedConfig) {
            value = value.replace(clientName + ".", "");
            Class<?> clazz = getComponentClass(value);
            if (clazz != null) {
                effectiveComponents.add(clazz);
                hasInterceptor = clazz.equals(RequestInterceptor.class);
            }
        }

        if (DEFAULT_CLIENT_NAME.equals(clientName)) {
            //default configs changed, need refresh all
            refreshableComponents.values().stream()
                    .flatMap(List::stream)
                    .filter(component -> {
                        Class<?> componentsClass = component.getClass();
                        for (Class<?> actualComponent : effectiveComponents)
                            if (actualComponent.isAssignableFrom(componentsClass))
                                return true;
                        return false;
                    })
                    .forEach(Refreshable::refresh);
            if (hasInterceptor)
                this.interceptorMap.values()
                    .forEach(CompositedRequestInterceptor::refresh);
            return;
        }
        List<Refreshable> components = this.refreshableComponents.get(clientName);
        if (components != null)
            components.stream()
                    .filter(component -> {
                        Class<?> componentsClass = component.getClass();
                        for (Class<?> actualComponent : effectiveComponents)
                            if (actualComponent.isAssignableFrom(componentsClass))
                                return true;
                        return false;
                    })
                    .forEach(Refreshable::refresh);

        if (hasInterceptor) {
            CompositedRequestInterceptor requestInterceptor = this.interceptorMap.get(clientName);
            if (requestInterceptor != null)
                requestInterceptor.refresh();
        }

    }

}
