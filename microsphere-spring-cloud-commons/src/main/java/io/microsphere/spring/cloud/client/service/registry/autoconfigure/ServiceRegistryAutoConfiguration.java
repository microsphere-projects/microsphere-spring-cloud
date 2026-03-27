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
package io.microsphere.spring.cloud.client.service.registry.autoconfigure;

import io.microsphere.spring.cloud.client.service.registry.MultipleAutoServiceRegistration;
import io.microsphere.spring.cloud.client.service.registry.MultipleRegistration;
import io.microsphere.spring.cloud.client.service.registry.MultipleServiceRegistry;
import io.microsphere.spring.cloud.client.service.registry.aspect.EventPublishingRegistrationAspect;
import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnAutoServiceRegistrationEnabled;
import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnMultipleRegistrationEnabled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.Collection;
import java.util.Map;

/**
 * Auto-Configuration class for {@link ServiceRegistry ServiceRegistry}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnAutoServiceRegistrationEnabled
@Import(value = {
        EventPublishingRegistrationAspect.class,
        ServiceRegistryAutoConfiguration.MultipleConfiguration.class
})
public class ServiceRegistryAutoConfiguration {

    /**
     * The configuration class of the multiple service registration
     */
    @ConditionalOnMultipleRegistrationEnabled
    static class MultipleConfiguration {

        /**
         * Creates a primary {@link MultipleRegistration} bean that aggregates all available
         * {@link Registration} instances.
         *
         * <p>Example Usage:
         * <pre>{@code
         * @Autowired
         * MultipleRegistration multipleRegistration;
         * // Access individual registrations from the composite
         * }</pre>
         *
         * @param registrations the collection of {@link Registration} instances
         * @return a new {@link MultipleRegistration} aggregating the provided registrations
         */
        @Primary
        @Bean
        @ConditionalOnMissingBean
        public MultipleRegistration multipleRegistration(Collection<Registration> registrations) {
            return new MultipleRegistration(registrations);
        }

        /**
         * Creates a primary {@link MultipleServiceRegistry} bean that delegates to all available
         * {@link ServiceRegistry} instances.
         *
         * <p>Example Usage:
         * <pre>{@code
         * @Autowired
         * MultipleServiceRegistry multipleServiceRegistry;
         * // Register with all service registries at once
         * multipleServiceRegistry.register(registration);
         * }</pre>
         *
         * @param registriesMap a map of bean names to {@link ServiceRegistry} instances
         * @return a new {@link MultipleServiceRegistry} delegating to all registries
         */
        @Bean
        @Primary
        @ConditionalOnMissingBean
        public MultipleServiceRegistry multipleServiceRegistry(Map<String, ServiceRegistry> registriesMap) {
            return new MultipleServiceRegistry(registriesMap);
        }

        /**
         * Creates a primary {@link MultipleAutoServiceRegistration} bean that manages auto-registration
         * across multiple service registries.
         *
         * <p>Example Usage:
         * <pre>{@code
         * @Autowired
         * MultipleAutoServiceRegistration autoRegistration;
         * // Auto-registration is managed by the Spring lifecycle
         * boolean running = autoRegistration.isRunning();
         * }</pre>
         *
         * @param multipleRegistration    the composite {@link MultipleRegistration}
         * @param multipleServiceRegistry the composite {@link MultipleServiceRegistry}
         * @param properties              the {@link AutoServiceRegistrationProperties}
         * @return a new {@link MultipleAutoServiceRegistration} instance
         */
        @ConditionalOnBean(AutoServiceRegistrationProperties.class)
        @Primary
        @Bean
        @ConditionalOnMissingBean
        public MultipleAutoServiceRegistration multipleAutoServiceRegistration(MultipleRegistration multipleRegistration,
                                                                               MultipleServiceRegistry multipleServiceRegistry,
                                                                               AutoServiceRegistrationProperties properties) {
            return new MultipleAutoServiceRegistration(multipleRegistration, multipleServiceRegistry, properties);
        }
    }

}