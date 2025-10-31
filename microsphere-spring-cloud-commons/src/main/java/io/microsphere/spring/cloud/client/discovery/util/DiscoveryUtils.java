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

package io.microsphere.spring.cloud.client.discovery.util;

import io.microsphere.annotation.Nonnull;
import io.microsphere.util.Utils;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryProperties;

import java.util.List;
import java.util.Map;

import static io.microsphere.reflect.MethodUtils.invokeMethod;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.setProperties;

/**
 * The utilities class for Spring Cloud Discovery
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Utils
 * @since 1.0.0
 */
public abstract class DiscoveryUtils implements Utils {

    /**
     * Get the instances map from {@link SimpleDiscoveryProperties}
     *
     * @param properties {@link SimpleDiscoveryProperties}
     * @return the instances map
     */
    @Nonnull
    public static Map<String, List<DefaultServiceInstance>> getInstancesMap(@Nonnull SimpleDiscoveryProperties properties) {
        return properties.getInstances();
    }

    /**
     * Get the instances map from {@link SimpleReactiveDiscoveryProperties}
     *
     * @param properties {@link SimpleReactiveDiscoveryProperties}
     * @return the instances map
     */
    @Nonnull
    public static Map<String, List<DefaultServiceInstance>> getInstancesMap(@Nonnull SimpleReactiveDiscoveryProperties properties) {
        return invokeMethod(properties, "getInstances");
    }

    /**
     * Convert {@link SimpleDiscoveryProperties} to {@link SimpleReactiveDiscoveryProperties}
     *
     * @param properties {@link SimpleDiscoveryProperties}
     * @return {@link SimpleReactiveDiscoveryProperties}
     */
    @Nonnull
    public static SimpleReactiveDiscoveryProperties simpleReactiveDiscoveryProperties(@Nonnull SimpleDiscoveryProperties properties) {
        SimpleReactiveDiscoveryProperties simpleReactiveDiscoveryProperties = new SimpleReactiveDiscoveryProperties();
        simpleReactiveDiscoveryProperties.setOrder(properties.getOrder());

        DefaultServiceInstance local = properties.getLocal();
        DefaultServiceInstance targetLocal = simpleReactiveDiscoveryProperties.getLocal();
        setProperties(targetLocal, local);

        Map<String, List<DefaultServiceInstance>> instances = getInstancesMap(properties);
        simpleReactiveDiscoveryProperties.setInstances(instances);

        return simpleReactiveDiscoveryProperties;
    }

    /**
     * Convert {@link SimpleReactiveDiscoveryProperties} to {@link SimpleDiscoveryProperties}
     *
     * @param properties {@link SimpleReactiveDiscoveryProperties}
     * @return {@link SimpleDiscoveryProperties}
     */
    @Nonnull
    public static SimpleDiscoveryProperties simpleDiscoveryProperties(@Nonnull SimpleReactiveDiscoveryProperties properties) {
        SimpleDiscoveryProperties simpleDiscoveryProperties = new SimpleDiscoveryProperties();
        simpleDiscoveryProperties.setOrder(properties.getOrder());

        DefaultServiceInstance local = properties.getLocal();
        simpleDiscoveryProperties.setInstance(local.getServiceId(), local.getHost(), local.getPort());

        Map<String, List<DefaultServiceInstance>> instances = invokeMethod(properties, "getInstances");
        simpleDiscoveryProperties.setInstances(instances);

        return simpleDiscoveryProperties;
    }

    private DiscoveryUtils() {
    }
}