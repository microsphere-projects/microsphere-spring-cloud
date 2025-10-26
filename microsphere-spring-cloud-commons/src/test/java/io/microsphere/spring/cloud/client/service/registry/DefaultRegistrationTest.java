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

import java.net.URI;

import static java.lang.System.currentTimeMillis;
import static java.net.URI.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link DefaultRegistration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DefaultRegistration
 * @since 1.0.0
 */
public class DefaultRegistrationTest {

    private DefaultRegistration registration;

    @BeforeEach
    void setUp() {
        this.registration = createDefaultRegistration();
    }

    @Test
    void testGetUri() {
        assertNotNull(this.registration.getUri());
    }

    @Test
    void testGetMetadata() {
        assertNotNull(this.registration.getMetadata());
    }

    @Test
    void testGetInstanceId() {
        assertNotNull(this.registration.getInstanceId());
    }

    @Test
    void testGetServiceId() {
        assertEquals("test-service", this.registration.getServiceId());
    }

    @Test
    void testGetHost() {
        assertEquals("localhost", this.registration.getHost());
    }

    @Test
    void testGetPort() {
        assertEquals(8080, this.registration.getPort());
    }

    @Test
    void testIsSecure() {
        assertFalse(this.registration.isSecure());
    }

    @Test
    void testSetInstanceId() {
        this.registration.setInstanceId("test-instance-id");
        assertEquals("test-instance-id", this.registration.getInstanceId());
    }

    @Test
    void testSetServiceId() {
        this.registration.setServiceId("test-service-id");
        assertEquals("test-service-id", this.registration.getServiceId());
    }

    @Test
    void testSetHost() {
        this.registration.setHost("test-host");
        assertEquals("test-host", this.registration.getHost());
    }

    @Test
    void testSetPort() {
        this.registration.setPort(9090);
        assertEquals(9090, this.registration.getPort());
    }

    @Test
    void testSetUri() {
        URI uri = create("http://localhost:9090");
        this.registration.setUri(uri);
        assertEquals(uri, this.registration.getUri());
    }

    @Test
    void testToString() {
        assertNotNull(this.registration.toString());
    }

    @Test
    void testEquals() {
        DefaultRegistration registration = createDefaultRegistration();
        registration.setInstanceId(this.registration.getInstanceId());
        assertEquals(this.registration, registration);
    }

    @Test
    void testHashCode() {
        DefaultRegistration registration = createDefaultRegistration();
        registration.setInstanceId(this.registration.getInstanceId());
        assertEquals(this.registration.hashCode(), registration.hashCode());

    }

    @Test
    void testGetScheme() {
        assertNull(this.registration.getScheme());
    }

    public static DefaultRegistration createDefaultRegistration() {
        DefaultRegistration defaultRegistration = new DefaultRegistration();
        defaultRegistration.setInstanceId("ServiceInstance-" + currentTimeMillis());
        defaultRegistration.setServiceId("test-service");
        defaultRegistration.setHost("localhost");
        defaultRegistration.setPort(8080);
        return defaultRegistration;
    }
}