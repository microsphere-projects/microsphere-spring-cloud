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

package io.microsphere.spring.cloud.client.service.registry.endpoint;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.lang.Integer.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ServiceRegistrationEndpoint} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServiceRegistrationEndpoint
 * @since 1.0.0
 */
@ContextConfiguration(classes = {
        ServiceRegistrationEndpoint.class
})
@EnableAutoConfiguration
class ServiceRegistrationEndpointTest extends BaseServiceRegistrationEndpointTest {

    @Autowired
    private ServiceRegistrationEndpoint endpoint;

    @Test
    void testMetadata() {
        Map<String, Object> metadata = this.endpoint.metadata();
        assertEquals("test-app", metadata.get("application-name"));
        assertSame(this.registration, metadata.get("registration"));
        assertEquals(this.port, metadata.get("port"));
        assertNull(metadata.get("status"));
        assertEquals(TRUE, metadata.get("running"));
        assertEquals(TRUE, metadata.get("enabled"));
        assertEquals(valueOf(0), metadata.get("phase"));
        assertEquals(valueOf(0), metadata.get("order"));
        assertSame(autoServiceRegistrationProperties, metadata.get("config"));
    }

    @Test
    void testStart() {
        assertFalse(this.endpoint.start());
        assertTrue(this.endpoint.start());
    }
}