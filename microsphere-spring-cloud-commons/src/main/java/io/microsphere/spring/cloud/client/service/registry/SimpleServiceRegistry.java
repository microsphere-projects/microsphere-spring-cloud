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
import org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryProperties;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.microsphere.spring.cloud.client.discovery.util.DiscoveryUtils.getInstancesMap;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.getMetadata;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.setMetadata;

/**
 * Simple {@link ServiceRegistry} class that is based on {@link SimpleDiscoveryProperties}
 * or {@link SimpleReactiveDiscoveryProperties} to register
 * {@link DefaultRegistration}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServiceRegistry
 * @see DefaultRegistration
 * @see SimpleDiscoveryProperties#getInstances()
 * @see SimpleReactiveDiscoveryProperties#getInstances()
 * @since 1.0.0
 */
public class SimpleServiceRegistry implements ServiceRegistry<DefaultRegistration> {

    public static final String STATUS_KEY = "_status_";

    private final Map<String, List<DefaultServiceInstance>> instancesMap;

    /**
     * Constructs a new {@link SimpleServiceRegistry} using the given
     * {@link SimpleDiscoveryProperties} to obtain the instances map.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleDiscoveryProperties properties = new SimpleDiscoveryProperties();
     * SimpleServiceRegistry registry = new SimpleServiceRegistry(properties);
     * }</pre>
     *
     * @param properties the {@link SimpleDiscoveryProperties} to use
     */
    public SimpleServiceRegistry(SimpleDiscoveryProperties properties) {
        this(getInstancesMap(properties));
    }

    /**
     * Constructs a new {@link SimpleServiceRegistry} using the given
     * {@link SimpleReactiveDiscoveryProperties} to obtain the instances map.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleReactiveDiscoveryProperties reactiveProperties = ...;
     * SimpleServiceRegistry registry = new SimpleServiceRegistry(reactiveProperties);
     * }</pre>
     *
     * @param properties the {@link SimpleReactiveDiscoveryProperties} to use
     */
    public SimpleServiceRegistry(SimpleReactiveDiscoveryProperties properties) {
        this(getInstancesMap(properties));
    }

    /**
     * Constructs a new {@link SimpleServiceRegistry} with the given instances map.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Map<String, List<DefaultServiceInstance>> instancesMap = new HashMap<>();
     * SimpleServiceRegistry registry = new SimpleServiceRegistry(instancesMap);
     * }</pre>
     *
     * @param instancesMap the map of service IDs to {@link DefaultServiceInstance} lists
     */
    public SimpleServiceRegistry(Map<String, List<DefaultServiceInstance>> instancesMap) {
        this.instancesMap = instancesMap;
    }

    /**
     * Registers the given {@link DefaultRegistration} by adding it to the instances list
     * for its service ID.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleServiceRegistry registry = new SimpleServiceRegistry(properties);
     * DefaultRegistration registration = new DefaultRegistration();
     * registration.setServiceId("test-service");
     * registry.register(registration);
     * }</pre>
     *
     * @param registration the {@link DefaultRegistration} to register
     */
    @Override
    public void register(DefaultRegistration registration) {
        List<DefaultServiceInstance> instances = getInstances(registration);
        instances.add(registration);
    }

    /**
     * Deregisters the given {@link DefaultRegistration} by removing it from the instances
     * list for its service ID.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleServiceRegistry registry = new SimpleServiceRegistry(properties);
     * registry.register(registration);
     * registry.deregister(registration);
     * }</pre>
     *
     * @param registration the {@link DefaultRegistration} to deregister
     */
    @Override
    public void deregister(DefaultRegistration registration) {
        List<DefaultServiceInstance> instances = getInstances(registration);
        instances.remove(registration);
    }

    /**
     * Closes this registry. This implementation is a no-op.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleServiceRegistry registry = new SimpleServiceRegistry(properties);
     * registry.close();
     * }</pre>
     */
    @Override
    public void close() {
    }

    /**
     * Sets the status of the given {@link DefaultRegistration} by storing it in the
     * registration's metadata under the {@link #STATUS_KEY} key.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleServiceRegistry registry = new SimpleServiceRegistry(properties);
     * registry.register(registration);
     * registry.setStatus(registration, "UP");
     * }</pre>
     *
     * @param registration the {@link DefaultRegistration} whose status is to be set
     * @param status       the status value to set
     */
    @Override
    public void setStatus(DefaultRegistration registration, String status) {
        setMetadata(registration, STATUS_KEY, status);
    }

    /**
     * Retrieves the status of the given {@link DefaultRegistration} from its metadata.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleServiceRegistry registry = new SimpleServiceRegistry(properties);
     * registry.register(registration);
     * registry.setStatus(registration, "UP");
     * String status = registry.getStatus(registration); // "UP"
     * }</pre>
     *
     * @param registration the {@link DefaultRegistration} whose status is to be retrieved
     * @return the status value, or {@code null} if not set
     */
    @Override
    public String getStatus(DefaultRegistration registration) {
        return getMetadata(registration, STATUS_KEY);
    }

    /**
     * Returns the list of {@link DefaultServiceInstance} instances for the given
     * {@link DefaultRegistration}'s service ID.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleServiceRegistry registry = new SimpleServiceRegistry(properties);
     * registry.register(registration);
     * List<DefaultServiceInstance> instances = registry.getInstances(registration);
     * }</pre>
     *
     * @param registration the {@link DefaultRegistration} to look up instances for
     * @return the list of instances for the registration's service ID
     */
    List<DefaultServiceInstance> getInstances(DefaultRegistration registration) {
        return getInstances(registration.getServiceId());
    }

    /**
     * Returns the list of {@link DefaultServiceInstance} instances for the given service ID,
     * creating an empty list if none exists.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleServiceRegistry registry = new SimpleServiceRegistry(properties);
     * List<DefaultServiceInstance> instances = registry.getInstances("test-service");
     * }</pre>
     *
     * @param serviceId the service ID to look up
     * @return the list of instances for the service ID
     */
    List<DefaultServiceInstance> getInstances(String serviceId) {
        return this.instancesMap.computeIfAbsent(serviceId, k -> new ArrayList<>());
    }
}