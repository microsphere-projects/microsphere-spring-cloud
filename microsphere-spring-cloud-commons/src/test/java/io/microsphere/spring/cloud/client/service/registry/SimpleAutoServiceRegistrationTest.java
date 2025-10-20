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


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link SimpleAutoServiceRegistration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SimpleAutoServiceRegistration
 * @since 1.0.0
 */
class SimpleAutoServiceRegistrationTest {

    private InMemoryServiceRegistry serviceRegistry;

    private AutoServiceRegistrationProperties properties;

    private DefaultRegistration registration;

    private SimpleAutoServiceRegistration autoServiceRegistration;

    @BeforeEach
    void setUp() {
        this.serviceRegistry = new InMemoryServiceRegistry();
        this.properties = new AutoServiceRegistrationProperties();
        this.registration = new DefaultRegistration();
        this.autoServiceRegistration = new SimpleAutoServiceRegistration(serviceRegistry, properties, registration);
    }

    @Test
    void testGetConfiguration() {
        assertSame(this.properties, autoServiceRegistration.getConfiguration());
    }

    @Test
    void testIsEnabled() {
        assertSame(this.properties.isEnabled(), autoServiceRegistration.isEnabled());
    }

    @Test
    void testGetRegistration() {
        assertSame(this.registration, autoServiceRegistration.getRegistration());
    }

    @Test
    void testGetManagementRegistration() {
        assertSame(this.registration, autoServiceRegistration.getManagementRegistration());
    }
}