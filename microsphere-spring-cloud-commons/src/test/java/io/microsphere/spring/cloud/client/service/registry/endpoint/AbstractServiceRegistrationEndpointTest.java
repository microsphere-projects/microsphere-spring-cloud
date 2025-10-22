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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.test.context.ContextConfiguration;

import static io.microsphere.spring.cloud.client.service.registry.endpoint.AbstractServiceRegistrationEndpoint.isRunning;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link AbstractServiceRegistrationEndpoint} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see BaseServiceRegistrationEndpointTest
 * @since 1.0.0
 */
@ContextConfiguration(classes = {
        AbstractServiceRegistrationEndpointTest.class
})
@EnableAutoConfiguration
class AbstractServiceRegistrationEndpointTest extends BaseServiceRegistrationEndpointTest {

    @Test
    void testOnApplicationEvent() {
        AbstractServiceRegistrationEndpoint endpoint = new AbstractServiceRegistrationEndpoint() {
        };
        WebServer webServer = mock(WebServer.class);
        when(webServer.getPort()).thenReturn(this.port);
        WebServerInitializedEvent event = new ServletWebServerInitializedEvent(webServer, null);
        endpoint.onApplicationEvent(event);
        assertFalse(endpoint.isRunning());
    }

    @Test
    void testIsRunning() {
        assertFalse(isRunning(null));
    }
}