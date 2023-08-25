/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microsphere.spring.cloud.gateway.filter;

import io.microsphere.spring.boot.context.config.BindableConfigurationBeanBinder;
import io.microsphere.spring.context.config.ConfigurationBeanBinder;
import io.microsphere.spring.web.metadata.WebEndpointMapping;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static io.microsphere.collection.PropertiesUtils.flatProperties;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.getWebEndpointMappings;
import static io.microsphere.spring.web.metadata.WebEndpointMapping.ID_HEADER_NAME;
import static io.microsphere.util.ArrayUtils.isNotEmpty;
import static java.net.URI.create;
import static org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.commaDelimitedListToSet;
import static org.springframework.web.reactive.result.method.RequestMappingInfo.paths;

/**
 * {@link WebEndpointMapping}  {@link GlobalFilter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ReactiveLoadBalancerClientFilter
 * @since 1.0.0
 */
public class WebEndpointMappingGlobalFilter implements GlobalFilter, ApplicationListener<RefreshRoutesResultEvent>,
        DisposableBean, Ordered {

    /**
     * The Web Endpoint scheme of the {@link Route#getUri() Routes' URI}
     */
    public static final String SCHEME = "we";

    /**
     * The all services for mapping
     */
    public static final String ALL_SERVICES = "all";

    /**
     * The key of the {@link Route#getMetadata() Routes' Metadata}
     */
    public static final String METADATA_KEY = "web-endpoint";

    private final DiscoveryClient discoveryClient;

    private volatile Map<String, Collection<RequestMappingContext>> routedRequestMappingContexts = null;

    private volatile Map<String, Config> routedConfigs = null;

    public WebEndpointMappingGlobalFilter(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);

        if (isInvalidRequest(url)) {
            // NO Web-Endpoint scheme
            return chain.filter(exchange);
        }

        RequestMappingContext requestMappingContext = getMatchingRequestMappingContext(exchange);

        if (requestMappingContext != null) {
            // The RequestMappingContext found
            ServiceInstance serviceInstance = requestMappingContext.choose();
            if (serviceInstance != null) {
                String basePath = buildBasePath(serviceInstance);
                URI targetURI = create(basePath + url.getPath());
                int id = requestMappingContext.id;
                ServerHttpRequest request = exchange.getRequest()
                        .mutate()
                        .header(ID_HEADER_NAME, String.valueOf(id)).build();
                exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, targetURI);
                return chain.filter(exchange.mutate().request(request).build());
            }
        }

        return chain.filter(exchange);
    }

    private RequestMappingContext getMatchingRequestMappingContext(ServerWebExchange exchange) {
        String routeId = getRouteId(exchange);

        if (isExcludedRequest(routeId, exchange)) {
            // The request is excluded
            return null;
        }

        Map<String, Collection<RequestMappingContext>> routedRequestMappingContexts = this.routedRequestMappingContexts;

        if (routedRequestMappingContexts == null) {
            // No RequestMappingContexts for routing
            return null;
        }

        Collection<RequestMappingContext> requestMappingContexts = routedRequestMappingContexts.get(routeId);

        if (isEmpty(requestMappingContexts)) {
            // No RequestMappingContext found
            return null;
        }

        RequestMappingContext target = null;

        for (RequestMappingContext requestMappingContext : requestMappingContexts) {
            if (matchesRequestMapping(exchange, requestMappingContext)) {
                // matches the request mapping
                target = requestMappingContext;
            }
        }
        return target;
    }

    private String getRouteId(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        return route == null ? null : route.getId();
    }

    private boolean isInvalidRequest(URI url) {
        return url == null || !SCHEME.equals(url.getScheme());
    }

    private boolean matchesRequestMapping(ServerWebExchange exchange, RequestMappingContext requestMappingContext) {
        RequestMappingInfo requestMappingInfo = requestMappingContext.requestMappingInfo;
        return requestMappingInfo.getMatchingCondition(exchange) != null;
    }

    private boolean isExcludedRequest(String routeId, ServerWebExchange exchange) {
        if (routeId == null) {
            return true;
        }
        Config config = routedConfigs.get(routeId);
        return config != null && config.isExcludedRequest(exchange);
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER - 1;
    }

    @Override
    public void onApplicationEvent(RefreshRoutesResultEvent event) {
        if (matchesEvent(event)) {
            Map<String, Collection<RequestMappingContext>> routedContexts = new HashMap<>();
            Map<String, Config> routedConfigs = new HashMap<>();
            RouteLocator routeLocator = (RouteLocator) event.getSource();
            routeLocator.getRoutes().filter(this::isWebEndpointRoute).subscribe((route -> {
                String routeId = route.getId();
                Config config = createConfig(route);
                routedConfigs.put(routeId, config);
                Map<WebEndpointMapping, RequestMappingContext> mappedContexts = new HashMap<>();
                getSubscribedServices(route, config)
                        .stream()
                        .map(discoveryClient::getInstances)
                        .flatMap(List::stream)
                        .forEach(serviceInstance -> {
                            getWebEndpointMappings(serviceInstance)
                                    .stream()
                                    .forEach(webEndpointMapping -> {
                                        RequestMappingContext requestMappingContext = mappedContexts.computeIfAbsent(webEndpointMapping, RequestMappingContext::new);
                                        requestMappingContext.addServiceInstance(serviceInstance);
                                    });
                        });
                routedContexts.put(routeId, mappedContexts.values());
            })).dispose();

            // exchange
            synchronized (this) {
                this.routedRequestMappingContexts = routedContexts;
                this.routedConfigs = routedConfigs;
            }
        }
    }

    private Config createConfig(Route route) {
        Map<String, Object> metadata = route.getMetadata();
        if (isEmpty(metadata)) {
            return null;
        }
        Map<String, Object> properties = (Map) metadata.get(METADATA_KEY);
        Map<String, Object> flatProperties = flatProperties(properties);
        ConfigurationBeanBinder beanBinder = new BindableConfigurationBeanBinder();
        Config config = new Config();
        beanBinder.bind(flatProperties, true, true, config);
        config.init();
        return config;
    }

    private boolean matchesEvent(RefreshRoutesResultEvent event) {
        return event.isSuccess() && (event.getSource() instanceof RouteLocator);
    }

    private Collection<String> getSubscribedServices(Route route, Config config) {
        Set<String> excludedServices = config.exclude.services;
        URI uri = route.getUri();
        String host = uri.getHost();
        final Collection<String> services = new LinkedList<>();
        if (ALL_SERVICES.equals(host)) {
            services.addAll(discoveryClient.getServices());
        } else {
            services.addAll(commaDelimitedListToSet(host));
        }
        services.removeAll(excludedServices);
        return services;
    }

    private boolean isWebEndpointRoute(Route route) {
        URI uri = route.getUri();
        return SCHEME.equals(uri.getScheme());
    }

    @Override
    public void destroy() throws Exception {
        if (routedRequestMappingContexts != null) {
            routedRequestMappingContexts.clear();
        }
        if (routedConfigs != null) {
            routedConfigs.clear();
        }
    }

    static class Config {

        Exclude exclude = new Exclude();

        String loadBalancer;

        RequestMappingInfo excludeRequestMappingInfo;

        public Exclude getExclude() {
            return exclude;
        }

        public void setExclude(Exclude exclude) {
            this.exclude = exclude;
        }

        public String getLoadBalancer() {
            return loadBalancer;
        }

        public void setLoadBalancer(String loadBalancer) {
            this.loadBalancer = loadBalancer;
        }

        public void init() {
            Exclude exclude = this.exclude;
            String[] patterns = exclude.patterns;
            if (isNotEmpty(patterns)) {
                this.excludeRequestMappingInfo = paths(patterns).methods(exclude.methods).build();
            }
        }

        public boolean isExcludedRequest(ServerWebExchange exchange) {
            return excludeRequestMappingInfo == null ? false :
                    excludeRequestMappingInfo.getMatchingCondition(exchange) != null;
        }

        static class Exclude {

            Set<String> services;

            String[] patterns = null;

            RequestMethod[] methods = null;

            public Set<String> getServices() {
                return services;
            }

            public void setServices(Set<String> services) {
                this.services = services;
            }

            public String[] getPatterns() {
                return patterns;
            }

            public void setPatterns(String[] patterns) {
                this.patterns = patterns;
            }

            public RequestMethod[] getMethods() {
                return methods;
            }

            public void setMethods(RequestMethod[] methods) {
                this.methods = methods;
            }
        }
    }

    static class RequestMappingContext {

        private final RequestMappingInfo requestMappingInfo;

        private int id;

        private List<ServiceInstance> serviceInstances = new LinkedList<>();

        private int size;

        private final AtomicInteger position = new AtomicInteger(0);

        RequestMappingContext(WebEndpointMapping webEndpointMapping) {
            this.requestMappingInfo = buildRequestMappingInfo(webEndpointMapping);
            this.id = webEndpointMapping.getId();
        }

        void addServiceInstance(ServiceInstance serviceInstance) {
            this.serviceInstances.add(serviceInstance);
        }

        ServiceInstance choose() {
            List<ServiceInstance> serviceInstances = this.serviceInstances;
            int size = serviceInstances.size();
            if (size == 0) {
                return null;
            }

            int offset = size == 1 ? 0 : this.position.incrementAndGet() % size;
            return serviceInstances.get(offset);
        }
    }

    private String buildBasePath(ServiceInstance serviceInstance) {
        StringBuilder basePathBuilder = new StringBuilder();
        basePathBuilder.append(serviceInstance.isSecure() ? "https://" : "http://")
                .append(serviceInstance.getHost())
                .append(":")
                .append(serviceInstance.getPort());
        // TODO append the context path
        return basePathBuilder.toString();
    }

    private static RequestMappingInfo buildRequestMappingInfo(WebEndpointMapping webEndpointMapping) {
        RequestMethod[] methods = buildRequestMethods(webEndpointMapping);
        return paths(webEndpointMapping.getPatterns())
                .methods(methods)
                .params(webEndpointMapping.getParams())
                .headers(webEndpointMapping.getHeaders())
                .consumes(webEndpointMapping.getConsumes())
                .produces(webEndpointMapping.getProduces())
                .build();

    }

    private static RequestMethod[] buildRequestMethods(WebEndpointMapping webEndpointMapping) {
        String[] methods = webEndpointMapping.getMethods();
        return methods == null ? null :
                Stream.of(webEndpointMapping.getMethods())
                        .map(RequestMethod::valueOf)
                        .toArray(RequestMethod[]::new);
    }
}
