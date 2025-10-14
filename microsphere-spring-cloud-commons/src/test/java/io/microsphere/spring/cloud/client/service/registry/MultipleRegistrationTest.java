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
import org.springframework.cloud.client.serviceregistry.Registration;

import static io.microsphere.collection.Lists.ofList;
import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link MultipleRegistration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see MultipleRegistration
 * @since 1.0.0
 */
class MultipleRegistrationTest {

    private DefaultRegistration defaultRegistration;

    private MultipleRegistration registration;

    @BeforeEach
    void setUp() {
        this.defaultRegistration = createDefaultRegistration();
        this.registration = new MultipleRegistration(ofList(defaultRegistration));
    }

    private DefaultRegistration createDefaultRegistration() {
        DefaultRegistration defaultRegistration = new DefaultRegistration();
        defaultRegistration.setInstanceId("ServiceInstance-" + currentTimeMillis());
        defaultRegistration.setServiceId("test-service");
        defaultRegistration.setHost("localhost");
        defaultRegistration.setPort(8080);
        defaultRegistration.setSecure(false);
        return defaultRegistration;
    }

    @Test
    void testGetInstanceId() {
        assertEquals(defaultRegistration.getInstanceId(), registration.getInstanceId());
    }

    @Test
    void testGetServiceId() {
        assertEquals(defaultRegistration.getServiceId(), registration.getServiceId());
    }

    @Test
    void testGetHost() {
        assertEquals(defaultRegistration.getHost(), registration.getHost());
    }

    @Test
    void testGetPort() {
        assertEquals(defaultRegistration.getPort(), registration.getPort());
    }

    @Test
    void testIsSecure() {
        assertEquals(defaultRegistration.isSecure(), registration.isSecure());
    }

    @Test
    void testGetUri() {
        assertEquals(defaultRegistration.getUri(), registration.getUri());
    }

    @Test
    void testGetMetadata() {
        assertEquals(defaultRegistration.getMetadata(), registration.getMetadata());
    }

    @Test
    void testGetDefaultRegistration() {
        assertEquals(defaultRegistration, registration.getDefaultRegistration());
    }

    @Test
    void testSpecial() {
        assertSame(registration, registration.special(Registration.class));
        assertSame(defaultRegistration, registration.special(DefaultRegistration.class));
    }
}