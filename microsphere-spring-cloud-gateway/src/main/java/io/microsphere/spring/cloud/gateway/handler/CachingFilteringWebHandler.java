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
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationListener;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.microsphere.invoke.MethodHandleUtils.LookupMode.ALL;
import static io.microsphere.util.ArrayUtils.asArray;
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

    private static final GatewayFilter[] EMPTY_FILTER_ARRAY = new GatewayFilter[0];

    private final GatewayFilter[] globalFilters;

    private volatile Map<String, GatewayFilter[]> routedGatewayFiltersCache = null;

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
            this.routedGatewayFiltersCache = buildRoutedGatewayFiltersCache(routeLocator);
        }
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
        GatewayFilter[] routedGatewayFilters = getRoutedGatewayFilters(route);
        return new DefaultGatewayFilterChain(routedGatewayFilters).filter(exchange);
    }

    @Override
    public void destroy() throws Exception {
        if (routedGatewayFiltersCache != null) {
            routedGatewayFiltersCache.clear();
        }
    }

    private Map<String, GatewayFilter[]> buildRoutedGatewayFiltersCache(RouteLocator routeLocator) {
        Map<String, GatewayFilter[]> routedGatewayFiltersCache = new HashMap<>();
        routeLocator.getRoutes().toStream().forEach(route -> {
            String routeId = route.getId();
            // TODO combinedGatewayFilters to be array ,instead of ArrayList
            GatewayFilter[] combinedGatewayFilters = combineGatewayFilters(route);
            routedGatewayFiltersCache.put(routeId, combinedGatewayFilters);
        });
        return routedGatewayFiltersCache;
    }

    private GatewayFilter[] getRoutedGatewayFilters(Route route) {
        Map<String, GatewayFilter[]> routedGatewayFiltersCache = this.routedGatewayFiltersCache;
        if (routedGatewayFiltersCache == null) {
            return EMPTY_FILTER_ARRAY;
        } else {
            String id = route.getId();
            return routedGatewayFiltersCache.getOrDefault(id, EMPTY_FILTER_ARRAY);
        }
    }

    private GatewayFilter[] combineGatewayFilters(Route route) {
        GatewayFilter[] globalFilters = getGlobalFilters();
        List<GatewayFilter> gatewayFilters = route.getFilters();
        int globalFiltersLength = globalFilters.length;
        int length = globalFiltersLength + gatewayFilters.size();
        GatewayFilter[] combinedGatewayFilters = new GatewayFilter[length];

        for (int i = 0; i < globalFiltersLength; i++) {
            combinedGatewayFilters[i] = globalFilters[i];
        }

        for (int i = globalFiltersLength, j = 0; i < length; i++) {
            combinedGatewayFilters[i] = gatewayFilters.get(j++);
        }

        sort(combinedGatewayFilters);
        return combinedGatewayFilters;
    }

    private boolean matchesEvent(RefreshRoutesResultEvent event) {
        return event.isSuccess() && (event.getSource() instanceof RouteLocator);
    }

    private GatewayFilter[] getGlobalFilters() {
        return globalFilters;
    }

    private GatewayFilter[] resolveGlobalFilters() {
        final List<GatewayFilter> globalFilters;
        try {
            globalFilters = (List<GatewayFilter>) globalFiltersMethodHandle.invoke(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return asArray(globalFilters, GatewayFilter.class);
    }
}
