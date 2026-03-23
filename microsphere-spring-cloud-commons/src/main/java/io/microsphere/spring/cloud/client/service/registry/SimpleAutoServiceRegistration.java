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
package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

/**
 * Default {@link AutoServiceRegistration}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SimpleAutoServiceRegistration extends AbstractAutoServiceRegistration<Registration> {

    private final AutoServiceRegistrationProperties properties;

    private final Registration registration;

    /**
     * Constructs a new {@link SimpleAutoServiceRegistration} with the specified
     * {@link ServiceRegistry}, {@link AutoServiceRegistrationProperties}, and
     * {@link Registration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * InMemoryServiceRegistry serviceRegistry = new InMemoryServiceRegistry();
     * AutoServiceRegistrationProperties properties = new AutoServiceRegistrationProperties();
     * Registration registration = createRegistration();
     * SimpleAutoServiceRegistration autoReg =
     *     new SimpleAutoServiceRegistration(serviceRegistry, properties, registration);
     * }</pre>
     *
     * @param serviceRegistry the {@link ServiceRegistry} to delegate to
     * @param properties      the {@link AutoServiceRegistrationProperties} for configuration
     * @param registration    the {@link Registration} to manage
     */
    public SimpleAutoServiceRegistration(ServiceRegistry<Registration> serviceRegistry,
                                            AutoServiceRegistrationProperties properties, Registration registration) {
        super(serviceRegistry, properties);
        this.properties = properties;
        this.registration = registration;
    }

    /**
     * Returns the {@link AutoServiceRegistrationProperties} as the configuration object.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleAutoServiceRegistration autoReg = ...;
     * Object config = autoReg.getConfiguration();
     * }</pre>
     *
     * @return the {@link AutoServiceRegistrationProperties} instance
     */
    @Override
    protected Object getConfiguration() {
        return properties;
    }

    /**
     * Determines whether this auto service registration is enabled based on the
     * {@link AutoServiceRegistrationProperties}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleAutoServiceRegistration autoReg = ...;
     * boolean enabled = autoReg.isEnabled();
     * }</pre>
     *
     * @return {@code true} if auto service registration is enabled
     */
    @Override
    protected boolean isEnabled() {
        return properties.isEnabled();
    }

    /**
     * Returns the {@link Registration} managed by this auto service registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleAutoServiceRegistration autoReg = ...;
     * Registration registration = autoReg.getRegistration();
     * }</pre>
     *
     * @return the {@link Registration} instance
     */
    @Override
    protected Registration getRegistration() {
        return registration;
    }

    /**
     * Returns the management {@link Registration}, which is the same as the primary
     * registration in this implementation.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleAutoServiceRegistration autoReg = ...;
     * Registration mgmtRegistration = autoReg.getManagementRegistration();
     * }</pre>
     *
     * @return the {@link Registration} instance used for management
     */
    @Override
    protected Registration getManagementRegistration() {
        return registration;
    }
}