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
package io.microsphere.spring.cloud.gateway.util;

import io.microsphere.spring.util.PropertySourcesUtils;
import io.microsphere.util.BaseUtils;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySources;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.microsphere.constants.SymbolConstants.DOT;
import static io.microsphere.spring.util.PropertySourcesUtils.getSubProperties;
import static io.microsphere.util.StringUtils.substringBeforeLast;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

/**
 * The utilities class for Gateway
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public abstract class GatewayUtils extends BaseUtils {

    /**
     * Get the flatten properties of {@link GatewayProperties} from the Spring {@link Environment}
     *
     * @param environment {@link ConfigurableEnvironment}
     * @return non-null
     */
    @NonNull
    public static Map<String, Object> getGatewayProperties(ConfigurableEnvironment environment) {
        return getSubProperties(environment, GatewayProperties.PREFIX);
    }

    /**
     * Get the flatten properties of the specified {@link RouteDefinition} in the {@link GatewayProperties}
     * from the Spring {@link Environment}
     *
     * @param environment {@link ConfigurableEnvironment}
     * @param routeId     the id of {@link RouteDefinition}
     * @return non-null
     */
    @NonNull
    public static Map<String, Object> getRouteProperties(ConfigurableEnvironment environment, String routeId) {
        Map<String, Object> gatewayProperties = getGatewayProperties(environment);
        String prefix = null;
        for (Map.Entry<String, Object> entry : gatewayProperties.entrySet()) {
            Object propertyValue = entry.getValue();
            if (Objects.equals(propertyValue, routeId)) {
                // matches the id of Route
                String propertyName = entry.getKey();
                prefix = substringBeforeLast(propertyName, DOT);
                break;
            }
        }

        if (prefix == null) { // the routeId can't be found
            return emptyMap();
        }

        Map<String, Object> routeProperties = new HashMap<>();

        for (Map.Entry<String, Object> entry : gatewayProperties.entrySet()) {
            String propertyName = entry.getKey();
            if (propertyName.startsWith(prefix)) {
                Object propertyValue = entry.getValue();
                routeProperties.put(propertyName, propertyValue);
            }
        }

        return unmodifiableMap(routeProperties);
    }
}
