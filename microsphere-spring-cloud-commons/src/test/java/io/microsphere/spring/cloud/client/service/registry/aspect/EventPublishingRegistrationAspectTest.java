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

package io.microsphere.spring.cloud.client.service.registry.aspect;


import io.microsphere.spring.cloud.client.service.registry.DefaultRegistration;
import io.microsphere.spring.cloud.client.service.registry.InMemoryServiceRegistry;
import io.microsphere.spring.cloud.client.service.registry.MultipleRegistration;
import io.microsphere.spring.cloud.client.service.registry.MultipleServiceRegistry;
import io.microsphere.spring.cloud.client.service.registry.RegistrationCustomizer;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationDeregisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreDeregisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreRegisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static io.microsphere.collection.Lists.ofList;
import static io.microsphere.spring.cloud.client.service.registry.DefaultRegistrationTest.createDefaultRegistration;
import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link EventPublishingRegistrationAspect} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EventPublishingRegistrationAspect
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        EventPublishingRegistrationAspect.class,
        EventPublishingRegistrationAspectTest.Config.class
})
@DirtiesContext
class EventPublishingRegistrationAspectTest {

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private ConfigurableApplicationContext context;

    private MultipleRegistration multipleRegistration;

    @BeforeEach
    void setUp() {
        DefaultRegistration defaultRegistration = createDefaultRegistration();
        defaultRegistration.setInstanceId("ServiceInstance-" + currentTimeMillis());
        defaultRegistration.setServiceId("test-service");
        defaultRegistration.setHost("localhost");
        defaultRegistration.setPort(8080);
        this.multipleRegistration = new MultipleRegistration(ofList(defaultRegistration));
    }

    @Test
    void testBeforeAndAfterRegister() {
        this.context.addApplicationListener((ApplicationListener<RegistrationPreRegisteredEvent>) event -> {
            assertRegistration(event);
        });
        this.context.addApplicationListener((ApplicationListener<RegistrationRegisteredEvent>) event -> {
            assertRegistration(event);
        });
        this.serviceRegistry.register(multipleRegistration);
    }

    @Test
    void testBeforeAndAfterDeregister() {
        this.context.addApplicationListener((ApplicationListener<RegistrationPreDeregisteredEvent>) event -> {
            assertRegistration(event);
        });
        this.context.addApplicationListener((ApplicationListener<RegistrationDeregisteredEvent>) event -> {
            assertRegistration(event);
        });
        this.serviceRegistry.deregister(multipleRegistration);
    }

    void assertRegistration(RegistrationEvent event) {
        assertSame(this.multipleRegistration, event.getRegistration());
    }

    @Import(InMemoryServiceRegistry.class)
    @EnableAspectJAutoProxy
    static class Config implements RegistrationCustomizer {

        @Override
        public void customize(Registration registration) {
        }

        @Bean
        @Primary
        public MultipleServiceRegistry multipleServiceRegistry(Map<String, ServiceRegistry> registriesMap) {
            return new MultipleServiceRegistry(registriesMap);
        }
    }
}