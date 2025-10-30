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

package io.microsphere.spring.cloud.client.discovery;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryClient;
import org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.microsphere.collection.Lists.ofList;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtilsTest.createDefaultServiceInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link ReactiveDiscoveryClientAdapter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ReactiveDiscoveryClientAdapter
 * @see ReactiveDiscoveryClient
 * @see DiscoveryClient
 * @since 1.0.0
 */
class ReactiveDiscoveryClientAdapterTest {

    private DefaultServiceInstance serviceInstance;

    private String appName = "test-service";

    private SimpleReactiveDiscoveryProperties properties;

    private ReactiveDiscoveryClient client;

    private ReactiveDiscoveryClientAdapter adapter;

    @BeforeEach
    void setUp() {
        Map<String, List<DefaultServiceInstance>> instances = new HashMap<>();
        this.serviceInstance = createDefaultServiceInstance();
        this.appName = this.serviceInstance.getServiceId();
        instances.put(appName, ofList(this.serviceInstance));
        this.properties = new SimpleReactiveDiscoveryProperties();
        this.properties.setInstances(instances);
        this.client = new SimpleReactiveDiscoveryClient(properties);
        this.adapter = new ReactiveDiscoveryClientAdapter(client);
    }

    @Test
    void testDescription() {
        assertEquals("Simple Reactive Discovery Client", this.adapter.description());
    }

    @Test
    void testGetInstances() {
        List<ServiceInstance> serviceInstances = this.adapter.getInstances(this.appName);
        assertEquals(1, serviceInstances.size());
        assertSame(this.serviceInstance, serviceInstances.get(0));
    }

    @Test
    void testGetServices() {
        List<String> services = this.adapter.getServices();
        assertEquals(1, services.size());
        assertSame(appName, services.get(0));
    }

    @Test
    void testProbe() {
        this.adapter.probe();
    }

    @Test
    void testGetOrder() {
        assertEquals(this.client.getOrder(), this.adapter.getOrder());
    }
}