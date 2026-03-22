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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import static io.microsphere.collection.CollectionUtils.isNotEmpty;
import static io.microsphere.reflect.TypeUtils.getClassName;
import static io.microsphere.spring.beans.BeanUtils.getSortedBeans;
import static io.microsphere.spring.cloud.client.discovery.constants.DiscoveryClientConstants.COMPOSITE_DISCOVERY_CLIENT_CLASS_NAME;

/**
 * The {@link DiscoveryClient} implementation for a union of the given {@link DiscoveryClient}
 *
 * <p>Example Usage:
 * <pre>{@code
 * // Register UnionDiscoveryClient as a Spring bean, then retrieve merged service instances
 * UnionDiscoveryClient unionDiscoveryClient = applicationContext.getBean(UnionDiscoveryClient.class);
 * List<ServiceInstance> instances = unionDiscoveryClient.getInstances("test");
 * List<String> services = unionDiscoveryClient.getServices();
 * }</pre>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CompositeDiscoveryClient
 * @since 1.0.0
 */
public final class UnionDiscoveryClient implements DiscoveryClient, ApplicationContextAware, SmartInitializingSingleton, DisposableBean {

    private ApplicationContext context;

    private List<DiscoveryClient> discoveryClients;

    /**
     * Returns a human-readable description of this {@link DiscoveryClient}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * String desc = unionDiscoveryClient.description();
     * }</pre>
     *
     * @return the description string {@code "Union Discovery Client"}
     */
    @Override
    public String description() {
        return "Union Discovery Client";
    }

    /**
     * Returns a merged list of {@link ServiceInstance} objects from all registered
     * {@link DiscoveryClient} instances for the given service ID.
     *
     * <p>Example Usage:
     * <pre>{@code
     * List<ServiceInstance> instances = unionDiscoveryClient.getInstances("test");
     * }</pre>
     *
     * @param serviceId the service ID to look up
     * @return the aggregated list of {@link ServiceInstance} from all discovery clients
     */
    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        List<ServiceInstance> serviceInstances = new LinkedList<>();
        List<DiscoveryClient> discoveryClients = getDiscoveryClients();
        for (DiscoveryClient discoveryClient : discoveryClients) {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            if (isNotEmpty(instances)) {
                serviceInstances.addAll(instances);
            }
        }
        return serviceInstances;
    }

    /**
     * Returns a deduplicated list of service names from all registered {@link DiscoveryClient}
     * instances, preserving insertion order.
     *
     * <p>Example Usage:
     * <pre>{@code
     * List<String> services = unionDiscoveryClient.getServices();
     * }</pre>
     *
     * @return the merged list of unique service names
     */
    @Override
    public List<String> getServices() {
        LinkedHashSet<String> services = new LinkedHashSet<>();
        List<DiscoveryClient> discoveryClients = getDiscoveryClients();
        for (DiscoveryClient discoveryClient : discoveryClients) {
            List<String> serviceForClient = discoveryClient.getServices();
            if (isNotEmpty(serviceForClient)) {
                services.addAll(serviceForClient);
            }
        }
        return new ArrayList<>(services);
    }

    /**
     * Returns the list of underlying {@link DiscoveryClient} instances, excluding
     * {@link CompositeDiscoveryClient} and this client itself. The list is lazily
     * initialized and cached.
     *
     * <p>Example Usage:
     * <pre>{@code
     * List<DiscoveryClient> clients = unionDiscoveryClient.getDiscoveryClients();
     * }</pre>
     *
     * @return the sorted list of registered {@link DiscoveryClient} instances
     */
    public List<DiscoveryClient> getDiscoveryClients() {
        List<DiscoveryClient> discoveryClients = this.discoveryClients;
        if (discoveryClients != null) {
            return discoveryClients;
        }

        discoveryClients = new ArrayList<>();
        for (DiscoveryClient discoveryClient : getSortedBeans(this.context, DiscoveryClient.class)) {
            String className = getClassName(discoveryClient.getClass());
            if (COMPOSITE_DISCOVERY_CLIENT_CLASS_NAME.equals(className) || this.equals(discoveryClient)) {
                // excludes CompositeDiscoveryClient and self
                continue;
            }
            discoveryClients.add(discoveryClient);
        }
        this.discoveryClients = discoveryClients;
        return discoveryClients;
    }

    /**
     * Returns the order value of this client. This client uses {@code HIGHEST_PRECEDENCE}
     * to ensure it takes priority over other {@link DiscoveryClient} implementations.
     *
     * <p>Example Usage:
     * <pre>{@code
     * int order = unionDiscoveryClient.getOrder();
     * }</pre>
     *
     * @return {@link org.springframework.core.Ordered#HIGHEST_PRECEDENCE}
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    /**
     * Callback invoked after all singleton beans have been instantiated. Eagerly initializes
     * the internal list of {@link DiscoveryClient} instances.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Automatically called by the Spring container after singleton initialization
     * unionDiscoveryClient.afterSingletonsInstantiated();
     * }</pre>
     */
    @Override
    public void afterSingletonsInstantiated() {
        this.discoveryClients = getDiscoveryClients();
    }

    /**
     * Clears the cached list of {@link DiscoveryClient} instances when this bean is destroyed.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Automatically called by the Spring container on shutdown
     * unionDiscoveryClient.destroy();
     * }</pre>
     *
     * @throws Exception if an error occurs during cleanup
     */
    @Override
    public void destroy() throws Exception {
        this.discoveryClients.clear();
    }

    /**
     * Sets the {@link ApplicationContext} used to look up {@link DiscoveryClient} beans.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Automatically called by the Spring container
     * unionDiscoveryClient.setApplicationContext(applicationContext);
     * }</pre>
     *
     * @param applicationContext the {@link ApplicationContext} to set
     * @throws BeansException if an error occurs while setting the context
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}