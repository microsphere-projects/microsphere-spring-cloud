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
package io.microsphere.spring.cloud.client.service.registry.actuate.autoconfigure;

import io.microsphere.spring.boot.actuate.condition.ConditionalOnActuatorEndpointPresent;
import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnAutoServiceRegistrationEnabled;
import io.microsphere.spring.cloud.client.service.registry.endpoint.ServiceDeregistrationEndpoint;
import io.microsphere.spring.cloud.client.service.registry.endpoint.ServiceRegistrationEndpoint;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.microsphere.spring.cloud.client.service.registry.constants.ServiceRegistryConstants.AUTO_SERVICE_REGISTRATION_CLASS_NAME;

/**
 * Microsphere {@link Endpoint @Endpoints} Auto-Configuration for Service Registration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see Endpoint
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnAutoServiceRegistrationEnabled
@ConditionalOnActuatorEndpointPresent
@ConditionalOnClass(name = {
        AUTO_SERVICE_REGISTRATION_CLASS_NAME
})
@AutoConfigureAfter(name = {
        "org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration",             // Spring Cloud Commons API
        "org.springframework.cloud.zookeeper.serviceregistry.ZookeeperServiceRegistryAutoConfiguration", // Spring Cloud Zookeeper Discovery API
        "org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration",       // Spring Cloud Consul Discovery API
        "org.springframework.cloud.kubernetes.discovery.KubernetesDiscoveryClientAutoConfiguration",     // Spring Cloud Kubernetes Discovery API
        "com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration",                        // Spring Cloud Alibaba Nacos Discovery API
})
public class ServiceRegistrationEndpointAutoConfiguration {

    /**
     * Creates a {@link ServiceRegistrationEndpoint} bean for managing service registration via actuator.
     *
     * @return a new {@link ServiceRegistrationEndpoint} instance
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public ServiceRegistrationEndpoint serviceRegistrationEndpoint() {
        return new ServiceRegistrationEndpoint();
    }

    /**
     * Creates a {@link ServiceDeregistrationEndpoint} bean for managing service deregistration via actuator.
     *
     * @return a new {@link ServiceDeregistrationEndpoint} instance
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public ServiceDeregistrationEndpoint serviceDeregistrationEndpoint() {
        return new ServiceDeregistrationEndpoint();
    }
}