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

package io.microsphere.spring.cloud.client.service.registry;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryProperties;

import java.util.List;
import java.util.Map;

import static io.microsphere.spring.cloud.client.discovery.util.DiscoveryUtils.simpleReactiveDiscoveryProperties;
import static io.microsphere.spring.cloud.client.service.registry.DefaultRegistrationTest.createDefaultRegistration;
import static io.microsphere.spring.cloud.client.service.registry.SimpleServiceRegistry.STATUS_KEY;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SimpleServiceRegistry} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SimpleServiceRegistry
 * @since 1.0.0
 */
class SimpleServiceRegistryTest {

    private DefaultRegistration registration;

    private SimpleDiscoveryProperties properties;

    private SimpleServiceRegistry registry;

    @BeforeEach
    void setUp() {
        this.registration = createDefaultRegistration();
        this.properties = new SimpleDiscoveryProperties();
        this.registry = new SimpleServiceRegistry(this.properties);
    }

    @Test
    void testConstructor() {
        SimpleReactiveDiscoveryProperties properties = simpleReactiveDiscoveryProperties(this.properties);
        this.registry = new SimpleServiceRegistry(properties);
        testDeregister();
    }

    @Test
    void testRegister() {
        Map<String, List<DefaultServiceInstance>> instancesMap = this.properties.getInstances();
        assertTrue(instancesMap.isEmpty());
        this.registry.register(this.registration);

        List<DefaultServiceInstance> instances = instancesMap.get(this.registration.getServiceId());
        assertEquals(1, instances.size());
        DefaultServiceInstance instance = instances.get(0);
        assertSame(instance, this.registration);
    }

    @Test
    void testDeregister() {
        testRegister();
        this.registry.deregister(this.registration);
        List<DefaultServiceInstance> instances = getInstances(this.registration.getServiceId());
        assertTrue(instances.isEmpty());
    }

    @Test
    void testClose() {
        Map<String, List<DefaultServiceInstance>> instancesMap = this.properties.getInstances();
        assertTrue(instancesMap.isEmpty());
        this.registry.close();
        assertTrue(instancesMap.isEmpty());
    }

    @Test
    void testSetStatus() {
        testRegister();
        DefaultServiceInstance instance = getInstance(this.registration.getServiceId(), this.registration.getInstanceId());
        String status = "UP";
        this.registry.setStatus(this.registration, status);
        assertEquals(status, instance.getMetadata().get(STATUS_KEY));
    }

    @Test
    void testGetStatus() {
        testSetStatus();
        assertEquals(this.registration.getMetadata().get(STATUS_KEY), this.registry.getStatus(this.registration));
    }

    List<DefaultServiceInstance> getInstances(String serviceId) {
        return this.properties.getInstances().getOrDefault(serviceId, emptyList());
    }

    DefaultServiceInstance getInstance(String serviceId, String instanceId) {
        List<DefaultServiceInstance> instances = getInstances(serviceId);
        return instances.stream()
                .filter(instance -> instance.getInstanceId().equals(instanceId))
                .findFirst().orElse(null);
    }
}