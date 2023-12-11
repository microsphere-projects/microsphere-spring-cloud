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

import io.microsphere.spring.cloud.client.service.registry.CompositeAutoServiceRegistration;
import io.microsphere.spring.cloud.client.service.registry.CompositeRegistration;
import io.microsphere.spring.cloud.client.service.registry.CompositeServiceRegistry;
import io.microsphere.spring.cloud.client.service.registry.RegistrationCustomizer;
import io.microsphere.spring.cloud.client.service.registry.aspect.EventPublishingRegistrationAspect;
import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnAutoServiceRegistrationEnabled;
import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnCompositeRegistrationEnabled;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.Map;

/**
 * Auto-Configuration class for the composite {@link ServiceRegistry ServiceRegistry}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnAutoServiceRegistrationEnabled
@ConditionalOnCompositeRegistrationEnabled
@Import(value = {
        EventPublishingRegistrationAspect.class
})
public class CompositeServiceRegistryAutoConfiguration {

    @Primary
    @Bean
    public CompositeRegistration multipleRegistration(Map<String, Registration> registrationsMap) {
        return new CompositeRegistration(registrationsMap);
    }

    @Primary
    @Bean
    public CompositeServiceRegistry compositeServiceRegistry(Map<String, ServiceRegistry> registriesMap,
                                                             ObjectProvider<RegistrationCustomizer> registrationCustomizers) {
        return new CompositeServiceRegistry(registriesMap, registrationCustomizers);
    }

    @ConditionalOnBean(AutoServiceRegistrationProperties.class)
    @Primary
    @Bean
    public CompositeAutoServiceRegistration compositeAutoServiceRegistration(
            CompositeRegistration compositeRegistration,
            CompositeServiceRegistry compositeServiceRegistry,
            AutoServiceRegistrationProperties properties) {
        return new CompositeAutoServiceRegistration(compositeRegistration, compositeServiceRegistry, properties);
    }

}
