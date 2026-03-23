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

import static io.microsphere.spring.cloud.client.service.registry.event.RegistrationEvent.Type.PRE_REGISTERED;

/**
 * The before-{@link ServiceRegistry#register(Registration) register} event.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServiceRegistry
 * @since 1.0.0
 */
public class RegistrationPreRegisteredEvent extends RegistrationEvent {

    /**
     * Create a new {@link RegistrationPreRegisteredEvent} indicating that a
     * {@link Registration} is about to be registered with the {@link ServiceRegistry}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * ServiceRegistry<Registration> registry = ...;
     * Registration registration = ...;
     * RegistrationPreRegisteredEvent event = new RegistrationPreRegisteredEvent(registry, registration);
     * applicationContext.publishEvent(event);
     * }</pre>
     *
     * @param registry the {@link ServiceRegistry} that will perform the registration
     * @param source   the {@link Registration} to be registered
     */
    public RegistrationPreRegisteredEvent(ServiceRegistry<Registration> registry, Registration source) {
        super(registry, source);
    }

    /**
     * Returns the {@link Type} of this event, which is always {@link Type#PRE_REGISTERED}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * RegistrationPreRegisteredEvent event = ...;
     * RegistrationEvent.Type type = event.getType();
     * // type == RegistrationEvent.Type.PRE_REGISTERED
     * }</pre>
     *
     * @return {@link Type#PRE_REGISTERED}
     */
    @Override
    public Type getType() {
        return PRE_REGISTERED;
    }
}