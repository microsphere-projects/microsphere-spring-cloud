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
package io.microsphere.spring.cloud.client.service.registry.event;

import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import static io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent.Type.DEREGISTERED;
import static io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent.Type.PRE_DEREGISTERED;
import static io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent.Type.PRE_REGISTERED;
import static io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent.Type.REGISTERED;

/**
 * The Spring event for {@link ServiceRegistry}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see RegistrationPreRegisteredEvent
 * @see RegistrationRegisteredEvent
 * @see RegistrationPreDeregisteredEvent
 * @see RegistrationDeregisteredEvent
 * @see ServiceRegistry
 * @since 1.0.0
 */
public abstract class RegistrationEvent extends ApplicationEvent {

    private final ServiceRegistry<Registration> registry;

    public RegistrationEvent(ServiceRegistry<Registration> registry, Registration source) {
        super(source);
        Assert.notNull(registry, "The 'registry' must not be null");
        this.registry = registry;
    }

    /**
     * Get the registration
     *
     * @return non-null
     */
    @Override
    public Registration getSource() {
        return (Registration) super.getSource();
    }

    /**
     * Get the registration
     *
     * @return non-null
     */
    @NonNull
    public Registration getRegistration() {
        return getSource();
    }

    /**
     * Get the {@link ServiceRegistry}
     *
     * @return non-null
     */
    @NonNull
    public ServiceRegistry<Registration> getRegistry() {
        return registry;
    }

    /**
     * Current event is raised before the {@link #getRegistration() registration} is
     * {@link ServiceRegistry#register(Registration) registered}.
     *
     * @return <code>true</code> if pre-registered
     */
    public final boolean isPreRegistered() {
        return getType() == PRE_REGISTERED;
    }

    /**
     * Current event is raised after the {@link #getRegistration() registration} is
     * {@link ServiceRegistry#register(Registration) registered}.
     *
     * @return <code>true</code> if registered
     */
    public final boolean isRegistered() {
        return getType() == REGISTERED;
    }

    /**
     * Current event is raised before the {@link #getRegistration() registration} is
     * {@link ServiceRegistry#deregister(Registration) deregistered}.
     *
     * @return <code>true</code> if pre-deregistered
     */
    public final boolean isPreDeregistered() {
        return getType() == PRE_DEREGISTERED;
    }

    /**
     * Current event is raised after the {@link #getRegistration() registration} is
     * {@link ServiceRegistry#deregister(Registration) deregistered}.
     *
     * @return <code>true</code> if deregistered
     */
    public final boolean isDeregistered() {
        return getType() == DEREGISTERED;
    }

    /**
     * Get the {@link Type} of the {@link RegistrationEvent}
     *
     * @return non-null
     */
    public abstract Type getType();

    /**
     * The {@link Type} of the {@link RegistrationEvent}
     */
    public static enum Type {

        /**
         * The {@link RegistrationPreRegisteredEvent}
         */
        PRE_REGISTERED,

        /**
         * The {@link RegistrationRegisteredEvent}
         */
        REGISTERED,

        /**
         * The {@link RegistrationPreDeregisteredEvent}
         */
        PRE_DEREGISTERED,

        /**
         * The {@link RegistrationDeregisteredEvent}
         */
        DEREGISTERED
    }
}