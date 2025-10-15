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
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import static io.microsphere.collection.Lists.ofList;
import static io.microsphere.collection.Maps.ofMap;
import static io.microsphere.spring.cloud.client.service.registry.MultipleRegistrationTest.createDefaultRegistration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link MultipleServiceRegistry} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see MultipleServiceRegistry
 * @since 1.0.0
 */
class MultipleServiceRegistryTest {

    private DefaultRegistration defaultRegistration;

    private MultipleRegistration registration;

    private ServiceRegistry<DefaultRegistration> serviceRegistry;

    private MultipleServiceRegistry multipleServiceRegistry;

    @BeforeEach
    void setUp() {
        this.defaultRegistration = createDefaultRegistration();
        this.registration = new MultipleRegistration(ofList(defaultRegistration));
        this.serviceRegistry = new InMemoryServiceRegistry();
        this.multipleServiceRegistry = new MultipleServiceRegistry(ofMap("default", serviceRegistry));
    }

    @Test
    void testRegister() {
        this.multipleServiceRegistry.register(this.registration);
    }

    @Test
    void testDeregister() {
        this.multipleServiceRegistry.deregister(this.registration);
    }

    @Test
    void testClose() {
        this.multipleServiceRegistry.close();
    }

    @Test
    void testStatus() {
        testRegister();
        this.multipleServiceRegistry.setStatus(this.registration, "UP");
        assertEquals("UP", this.multipleServiceRegistry.getStatus(this.registration));
        testDeregister();
        this.multipleServiceRegistry.setStatus(this.registration, "UP");
        assertNull(this.multipleServiceRegistry.getStatus(this.registration));
    }
}