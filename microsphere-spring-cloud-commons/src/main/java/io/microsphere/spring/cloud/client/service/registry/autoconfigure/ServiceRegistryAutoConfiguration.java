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

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import io.microsphere.spring.cloud.client.service.registry.MultipleRegistration;
import io.microsphere.spring.cloud.client.service.registry.MultipleServiceRegistry;
import io.microsphere.spring.cloud.client.service.registry.aspect.EventPublishingRegistrationAspect;
import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnAutoServiceRegistrationEnabled;
import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnMultipleRegistrationEnabled;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * Auto-Configuration class for {@link ServiceRegistry ServiceRegistry}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnAutoServiceRegistrationEnabled
@Import(value = {
        EventPublishingRegistrationAspect.class
})
public class ServiceRegistryAutoConfiguration {

    @ConditionalOnMultipleRegistrationEnabled
    @Primary
    @Bean
    public MultipleRegistration multipleRegistration(List<Registration> registrationProvider) {
        return new MultipleRegistration(NacosRegistration.class, registrationProvider);
    }

    @ConditionalOnMultipleRegistrationEnabled
    @Primary
    @Bean
    public MultipleServiceRegistry multipleServiceRegistry(List<ServiceRegistry> serviceRegistries) {
        return new MultipleServiceRegistry(NacosServiceRegistry.class, serviceRegistries);
    }

}
