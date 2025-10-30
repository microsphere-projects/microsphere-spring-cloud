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

import io.microsphere.spring.cloud.client.service.registry.MultipleServiceRegistry;
import io.microsphere.spring.cloud.client.service.registry.RegistrationCustomizer;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationDeregisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreDeregisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationPreRegisteredEvent;
import io.microsphere.spring.cloud.client.service.registry.event.RegistrationRegisteredEvent;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Event-Publishing Aspect for {@link Registration}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see RegistrationPreRegisteredEvent
 * @see RegistrationRegisteredEvent
 * @see RegistrationPreDeregisteredEvent
 * @see RegistrationDeregisteredEvent
 * @since 1.0.0
 */
@Aspect
public class EventPublishingRegistrationAspect implements ApplicationContextAware {

    /**
     * The pointcut expression for {@link ServiceRegistry#register(Registration)}.
     */
    public static final String REGISTER_POINTCUT_EXPRESSION = "execution(* org.springframework.cloud.client.serviceregistry.ServiceRegistry.register(*)) && target(registry) && args(registration)";

    /**
     * The pointcut expression for {@link ServiceRegistry#deregister(Registration)}.
     */
    public static final String DEREGISTER_POINTCUT_EXPRESSION = "execution(* org.springframework.cloud.client.serviceregistry.ServiceRegistry.deregister(*)) && target(registry) && args(registration)";

    private ApplicationContext context;

    private ObjectProvider<RegistrationCustomizer> registrationCustomizers;

    @Before(value = REGISTER_POINTCUT_EXPRESSION, argNames = "registry, registration")
    public void beforeRegister(ServiceRegistry registry, Registration registration) {
        if (isIgnored(registry)) {
            return; // Remove redundant deregister
        }
        context.publishEvent(new RegistrationPreRegisteredEvent(registry, registration));
        registrationCustomizers.forEach(customizer -> {
            customizer.customize(registration);
        });
    }

    @Before(value = DEREGISTER_POINTCUT_EXPRESSION, argNames = "registry, registration")
    public void beforeDeregister(ServiceRegistry registry, Registration registration) {
        if (isIgnored(registry)) {
            return; // Remove redundant deregister
        }
        context.publishEvent(new RegistrationPreDeregisteredEvent(registry, registration));
    }

    @After(value = REGISTER_POINTCUT_EXPRESSION, argNames = "registry, registration")
    public void afterRegister(ServiceRegistry registry, Registration registration) {
        if (isIgnored(registry)) {
            return; // Remove redundant deregister
        }
        context.publishEvent(new RegistrationRegisteredEvent(registry, registration));
    }

    @After(value = DEREGISTER_POINTCUT_EXPRESSION, argNames = "registry, registration")
    public void afterDeregister(ServiceRegistry registry, Registration registration) {
        if (isIgnored(registry)) {
            return; // Remove redundant deregister
        }
        context.publishEvent(new RegistrationDeregisteredEvent(registry, registration));
    }

    boolean isIgnored(ServiceRegistry registry) {
        return MultipleServiceRegistry.class.isAssignableFrom(registry.getClass());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        this.registrationCustomizers = applicationContext.getBeanProvider(RegistrationCustomizer.class);
    }
}