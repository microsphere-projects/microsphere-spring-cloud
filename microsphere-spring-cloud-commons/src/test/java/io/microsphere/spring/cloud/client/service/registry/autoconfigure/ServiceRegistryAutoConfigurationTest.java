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


import io.microsphere.spring.boot.test.AutoConfigurationTest;
import io.microsphere.spring.cloud.client.service.registry.aspect.EventPublishingRegistrationAspect;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.util.Set;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

/**
 * {@link ServiceRegistryAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServiceRegistryAutoConfiguration
 * @since 1.0.0
 */
@SpringBootTest(classes = {
        ServiceRegistryAutoConfigurationTest.class
}, webEnvironment = NONE)
class ServiceRegistryAutoConfigurationTest extends AutoConfigurationTest<ServiceRegistryAutoConfiguration> {

    @Override
    protected void configureAutoConfiguredClasses(Set<Class<?>> autoConfiguredClasses) {
        autoConfiguredClasses.add(EventPublishingRegistrationAspect.class);
    }

    @Override
    protected void configureGlobalDisabledPropertyValues(Set<String> globalDisabledPropertyValues) {
        globalDisabledPropertyValues.add("spring.cloud.service-registry.auto-registration.enabled=false");
    }

    @Override
    protected void configureGlobalMissingClasses(Set<Class<?>> globalMissingClasses) {
        globalMissingClasses.add(Aspect.class);
        globalMissingClasses.add(Registration.class);
    }
}