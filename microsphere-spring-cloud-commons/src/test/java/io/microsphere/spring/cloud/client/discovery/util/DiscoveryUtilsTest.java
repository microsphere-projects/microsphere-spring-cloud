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


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryProperties;

import java.util.List;
import java.util.Map;

import static io.microsphere.collection.Lists.ofList;
import static io.microsphere.spring.cloud.client.discovery.util.DiscoveryUtils.getInstancesMap;
import static io.microsphere.spring.cloud.client.discovery.util.DiscoveryUtils.simpleDiscoveryProperties;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtilsTest.createDefaultServiceInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DiscoveryUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DiscoveryUtils
 * @since 1.0.0
 */
class DiscoveryUtilsTest {

    private DefaultServiceInstance serviceInstance;

    private SimpleDiscoveryProperties properties;

    @BeforeEach
    void setUp() {
        this.serviceInstance = createDefaultServiceInstance();
        this.properties = new SimpleDiscoveryProperties();
        Map<String, List<DefaultServiceInstance>> instancesMap = this.properties.getInstances();
        instancesMap.put(this.serviceInstance.getInstanceId(), ofList(this.serviceInstance));
    }

    @Test
    void testGetInstancesMap() {
        Map<String, List<DefaultServiceInstance>> instancesMap = getInstancesMap(this.properties);
        assertEquals(1, instancesMap.size());
        assertEquals(ofList(this.serviceInstance), instancesMap.get(this.serviceInstance.getInstanceId()));
    }

    @Test
    void testGetInstancesMapFromSimpleReactiveDiscoveryProperties() {
        SimpleReactiveDiscoveryProperties properties = new SimpleReactiveDiscoveryProperties();
        Map<String, List<DefaultServiceInstance>> instancesMap = getInstancesMap(properties);
        assertTrue(instancesMap.isEmpty());

        properties.setInstances(this.properties.getInstances());
        instancesMap = getInstancesMap(properties);
        assertEquals(1, instancesMap.size());
        assertEquals(ofList(this.serviceInstance), instancesMap.get(this.serviceInstance.getInstanceId()));
    }

    @Test
    void testSimpleDiscoveryProperties() {
        SimpleReactiveDiscoveryProperties properties = new SimpleReactiveDiscoveryProperties();
        properties.setInstances(this.properties.getInstances());

        SimpleDiscoveryProperties simpleDiscoveryProperties = simpleDiscoveryProperties(properties);
        assertEquals(this.properties.getInstances(), simpleDiscoveryProperties.getInstances());
    }
}