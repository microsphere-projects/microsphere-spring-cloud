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
     * Constructs a new {@link SimpleAutoServiceRegistration} with the given service registry,
     * properties, and registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * SimpleAutoServiceRegistration autoReg = new SimpleAutoServiceRegistration(
     *     serviceRegistry, properties, registration);
     * }</pre>
     *
     * @param serviceRegistry the {@link ServiceRegistry} to register with
     * @param properties      the {@link AutoServiceRegistrationProperties} configuration
     * @param registration    the {@link Registration} to auto-register
     */
    public SimpleAutoServiceRegistration(ServiceRegistry<Registration> serviceRegistry,
                                            AutoServiceRegistrationProperties properties, Registration registration) {
        super(serviceRegistry, properties);
        this.properties = properties;
        this.registration = registration;
    }

    /**
     * Returns the configuration object for this auto-registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Object config = autoRegistration.getConfiguration();
     * }</pre>
     *
     * @return the {@link AutoServiceRegistrationProperties} instance
     */
    @Override
    protected Object getConfiguration() {
        return properties;
    }

    /**
     * Returns whether auto service registration is enabled.
     *
     * <p>Example Usage:
     * <pre>{@code
     * boolean enabled = autoRegistration.isEnabled();
     * }</pre>
     *
     * @return {@code true} if auto service registration is enabled
     */
    @Override
    protected boolean isEnabled() {
        return properties.isEnabled();
    }

    /**
     * Returns the {@link Registration} used for service registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Registration reg = autoRegistration.getRegistration();
     * }</pre>
     *
     * @return the {@link Registration} instance
     */
    @Override
    protected Registration getRegistration() {
        return registration;
    }

    /**
     * Returns the {@link Registration} used for management service registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Registration mgmtReg = autoRegistration.getManagementRegistration();
     * }</pre>
     *
     * @return the {@link Registration} instance for management
     */
    @Override
    protected Registration getManagementRegistration() {
        return registration;
    }
}