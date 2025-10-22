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
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import static io.microsphere.collection.Lists.ofList;
import static io.microsphere.spring.cloud.client.service.registry.DefaultRegistrationTest.createDefaultRegistration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link MultipleAutoServiceRegistration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see MultipleAutoServiceRegistration
 * @since 1.0.0
 */
class MultipleAutoServiceRegistrationTest {

    private DefaultRegistration defaultRegistration;

    private MultipleRegistration registration;

    private ServiceRegistry<MultipleRegistration> serviceRegistry;

    private AutoServiceRegistrationProperties properties;

    private MultipleAutoServiceRegistration autoServiceRegistration;

    @BeforeEach
    void setUp() {
        this.defaultRegistration = createDefaultRegistration();
        this.registration = new MultipleRegistration(ofList(defaultRegistration));
        this.serviceRegistry = new InMemoryServiceRegistry();
        this.properties = new AutoServiceRegistrationProperties();
        this.autoServiceRegistration = new MultipleAutoServiceRegistration(registration, serviceRegistry, properties);
    }

    @Test
    void testGetConfiguration() {
        assertNull(this.autoServiceRegistration.getConfiguration());
    }

    @Test
    void testIsEnabled() {
        assertEquals(this.properties.isEnabled(), this.autoServiceRegistration.isEnabled());
    }

    @Test
    void testGetRegistration() {
        assertSame(this.registration, this.autoServiceRegistration.getRegistration());
    }

    @Test
    void testGetManagementRegistration() {
        assertSame(this.registration, this.autoServiceRegistration.getManagementRegistration());
    }
}