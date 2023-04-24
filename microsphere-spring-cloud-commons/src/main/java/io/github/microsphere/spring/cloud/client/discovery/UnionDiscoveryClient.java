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
package io.github.microsphere.spring.cloud.client.discovery;

import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import static io.github.microsphere.spring.cloud.client.discovery.constants.DiscoveryClientConstants.COMPOSITE_DISCOVERY_CLIENT_CLASS_NAME;

/**
 * The {@link DiscoveryClient} implementation for a union of the given {@link DiscoveryClient}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CompositeDiscoveryClient
 * @since 1.0.0
 */
public final class UnionDiscoveryClient implements DiscoveryClient {

    private final ObjectProvider<DiscoveryClient> discoveryClientsProvider;

    public UnionDiscoveryClient(ObjectProvider<DiscoveryClient> discoveryClientsProvider) {
        this.discoveryClientsProvider = discoveryClientsProvider;
    }

    @Override
    public String description() {
        return "Union Discovery Client";
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        List<ServiceInstance> serviceInstances = new LinkedList<>();
        List<DiscoveryClient> discoveryClients = getDiscoveryClients();
        for (DiscoveryClient discoveryClient : discoveryClients) {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            if (instances != null && !instances.isEmpty()) {
                serviceInstances.addAll(instances);
            }
        }
        return serviceInstances;
    }

    @Override
    public List<String> getServices() {
        LinkedHashSet<String> services = new LinkedHashSet<>();
        List<DiscoveryClient> discoveryClients = getDiscoveryClients();
        for (DiscoveryClient discoveryClient : discoveryClients) {
            List<String> serviceForClient = discoveryClient.getServices();
            if (serviceForClient != null) {
                services.addAll(serviceForClient);
            }
        }
        return new ArrayList<>(services);
    }

    public List<DiscoveryClient> getDiscoveryClients() {
        List<DiscoveryClient> discoveryClients = new LinkedList<>();
        for (DiscoveryClient discoveryClient : discoveryClientsProvider) {
            String className = ClassUtils.getName(discoveryClient);
            if (COMPOSITE_DISCOVERY_CLIENT_CLASS_NAME.equals(className) || this.equals(discoveryClient)) {
                // excludes CompositeDiscoveryClient and self
                continue;
            }
            discoveryClients.add(discoveryClient);
        }
        return discoveryClients;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
