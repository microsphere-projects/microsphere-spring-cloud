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
package io.microsphere.spring.cloud.gateway.handler;

import io.microsphere.invoke.MethodHandleUtils;
import io.microsphere.spring.cloud.gateway.filter.DefaultGatewayFilterChain;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.microsphere.invoke.MethodHandleUtils.LookupMode.ALL;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static org.springframework.core.annotation.AnnotationAwareOrderComparator.sort;

/**
 * {@link FilteringWebHandler} extension class caches the {@link GlobalFilter GlobalFilters} and
 * the {@link GatewayFilter GatewayFilters} from the matched {@link Route Routes} when
 * {@link #handle(ServerWebExchange) handle} the request
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see FilteringWebHandler
 * @see GlobalFilter
 * @see GatewayFilter
 * @see Route
 * @see RouteLocator
 * @see RefreshRoutesResultEvent
 * @since 1.0.0
 */
public class CachingFilteringWebHandler extends FilteringWebHandler implements ApplicationListener<RefreshRoutesResultEvent>,
        DisposableBean {

    private static final MethodHandles.Lookup lookup = MethodHandleUtils.lookup(FilteringWebHandler.class, ALL);

    private static final MethodHandle globalFiltersMethodHandle;

    private final List<GatewayFilter> globalFilters;

    private volatile Map<String, GatewayFilterChain> routedFilterChainsCache = null;

    static {
        try {
            globalFiltersMethodHandle = lookup.findGetter(FilteringWebHandler.class, "globalFilters", List.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CachingFilteringWebHandler(List<GlobalFilter> globalFilters) {
        super(globalFilters);
        this.globalFilters = resolveGlobalFilters();
    }

    @Override
    public void onApplicationEvent(RefreshRoutesResultEvent event) {
        if (matchesEvent(event)) {
            RouteLocator routeLocator = (RouteLocator) event.getSource();
            this.routedFilterChainsCache = buildRoutedFilterChainCache(routeLocator);
        }
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        GatewayFilterChain gatewayFilterChain = getGatewayFilterChain(route);
        return gatewayFilterChain.filter(exchange);
    }

    @Override
    public void destroy() throws Exception {
        if (routedFilterChainsCache != null) {
            routedFilterChainsCache.clear();
        }
    }

    private Map<String, List<GatewayFilter>> buildRoutedGatewayFiltersCache(RouteLocator routeLocator) {
        Map<String, List<GatewayFilter>> routedGatewayFiltersCache = new HashMap<>();
        routeLocator.getRoutes().toStream().forEach(route -> {
            List<GatewayFilter> globalFilters = getGlobalFilters();
            List<GatewayFilter> gatewayFilters = route.getFilters();
            List<GatewayFilter> combined = new ArrayList<>(gatewayFilters.size() + gatewayFilters.size());
            combined.addAll(globalFilters);
            combined.addAll(gatewayFilters);
            sort(combined);

            String routeId = route.getId();
            routedGatewayFiltersCache.put(routeId, combined);
        });
        return routedGatewayFiltersCache;
    }

    @Nullable
    private GatewayFilterChain getGatewayFilterChain(Route route) {
        String id = route.getId();
        Map<String, GatewayFilterChain> routedFilterChainsCache = this.routedFilterChainsCache;
        return routedFilterChainsCache == null ? null : routedFilterChainsCache.get(id);
    }

    private Map<String, GatewayFilterChain> buildRoutedFilterChainCache(RouteLocator routeLocator) {
        Map<String, GatewayFilterChain> routedFilterChainsCache = new HashMap<>();
        routeLocator.getRoutes().toStream().forEach(route -> {
            String id = route.getId();
            List<GatewayFilter> combinedGatewayFilters = combineGatewayFilters(route);
            GatewayFilterChain gatewayFilterChain = new DefaultGatewayFilterChain(combinedGatewayFilters);
            routedFilterChainsCache.put(id, gatewayFilterChain);
        });
        return routedFilterChainsCache;
    }


    private List<GatewayFilter> combineGatewayFilters(Route route) {
        List<GatewayFilter> globalFilters = getGlobalFilters();
        List<GatewayFilter> gatewayFilters = route.getFilters();
        List<GatewayFilter> combinedGatewayFilters = new ArrayList<>(gatewayFilters.size() + gatewayFilters.size());
        combinedGatewayFilters.addAll(globalFilters);
        combinedGatewayFilters.addAll(gatewayFilters);
        sort(combinedGatewayFilters);
        return combinedGatewayFilters;
    }

    private boolean matchesEvent(RefreshRoutesResultEvent event) {
        return event.isSuccess() && (event.getSource() instanceof RouteLocator);
    }

    private List<GatewayFilter> getGlobalFilters() {
        return globalFilters;
    }

    private List<GatewayFilter> resolveGlobalFilters() {
        final List<GatewayFilter> globalFilters;
        try {
            globalFilters = (List<GatewayFilter>) globalFiltersMethodHandle.invoke(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return globalFilters;
    }
}
