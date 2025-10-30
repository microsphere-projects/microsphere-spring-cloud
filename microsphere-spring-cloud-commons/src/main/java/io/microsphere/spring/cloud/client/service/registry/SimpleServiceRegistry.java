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

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Simple {@link ServiceRegistry} class that is based on {@link SimpleDiscoveryProperties} to register
 * {@link DefaultRegistration}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServiceRegistry
 * @see DefaultRegistration
 * @see SimpleDiscoveryProperties#getInstances()
 * @since 1.0.0
 */
public class SimpleServiceRegistry implements ServiceRegistry<DefaultRegistration> {

    public static final String STATUS_KEY = "_status_";

    private final Map<String, List<DefaultServiceInstance>> instancesMap;

    public SimpleServiceRegistry(SimpleDiscoveryProperties simpleDiscoveryProperties) {
        this.instancesMap = simpleDiscoveryProperties.getInstances();
    }

    @Override
    public void register(DefaultRegistration registration) {
        List<DefaultServiceInstance> instances = getInstances(registration);
        instances.add(registration);
    }

    @Override
    public void deregister(DefaultRegistration registration) {
        List<DefaultServiceInstance> instances = getInstances(registration);
        instances.remove(registration);
    }

    @Override
    public void close() {
    }

    @Override
    public void setStatus(DefaultRegistration registration, String status) {
        Map<String, String> metadata = getMetadata(registration);
        metadata.put(STATUS_KEY, status);
    }

    @Override
    public <T> T getStatus(DefaultRegistration registration) {
        Map<String, String> metadata = getMetadata(registration);
        return (T) metadata.get(STATUS_KEY);
    }

    List<DefaultServiceInstance> getInstances(DefaultRegistration registration) {
        return getInstances(registration.getServiceId());
    }

    List<DefaultServiceInstance> getInstances(String serviceId) {
        return this.instancesMap.computeIfAbsent(serviceId, k -> new ArrayList<>());
    }

    Map<String, String> getMetadata(Registration registration) {
        return registration.getMetadata();
    }
}