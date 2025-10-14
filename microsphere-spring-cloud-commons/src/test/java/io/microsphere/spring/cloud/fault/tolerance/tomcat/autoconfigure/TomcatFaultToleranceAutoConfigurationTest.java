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
package io.microsphere.spring.cloud.fault.tolerance.tomcat.autoconfigure;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.cloud.context.environment.EnvironmentManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link TomcatFaultToleranceAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@SpringBootTest(classes = {
        TomcatFaultToleranceAutoConfigurationTest.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
class TomcatFaultToleranceAutoConfigurationTest {

    @Autowired
    private EnvironmentManager environmentManager;

    @Autowired
    private WebServerApplicationContext context;

    private Tomcat tomcat;

    private Connector connector;

    private AbstractHttp11Protocol protocol;

    @BeforeEach
    void before() {
        TomcatWebServer tomcatWebServer = (TomcatWebServer) context.getWebServer();
        this.tomcat = tomcatWebServer.getTomcat();
        this.connector = tomcat.getConnector();
        this.protocol = (AbstractHttp11Protocol) connector.getProtocolHandler();
    }

    @Test
    void testMinSpareThreads() {
        // default
        assertEquals(10, protocol.getMinSpareThreads());
        // changed
        environmentManager.setProperty("server.tomcat.threads.min-spare", "20");
        assertEquals(20, protocol.getMinSpareThreads());
    }

    @Test
    void testMaxThreads() {
        // default
        assertEquals(200, protocol.getMaxThreads());
        // changed
        environmentManager.setProperty("server.tomcat.threads.max", "128");
        assertEquals(128, protocol.getMaxThreads());
    }

    @Test
    void testAcceptCount() {
        // default
        assertEquals(100, protocol.getAcceptCount());
        // changed
        environmentManager.setProperty("server.tomcat.accept-count", "1000");
        assertEquals(1000, protocol.getAcceptCount());
    }

    @Test
    void testConnectionTimeout() {
        // default
        assertEquals(60000, protocol.getConnectionTimeout());
        // changed
        environmentManager.setProperty("server.tomcat.connection-timeout", "1000");
        assertEquals(1000, protocol.getConnectionTimeout());
    }

    @Test
    void testMaxConnections() {
        // default
        assertEquals(8192, protocol.getMaxConnections());
        // changed
        environmentManager.setProperty("server.tomcat.max-connections", "5120");
        assertEquals(5120, protocol.getMaxConnections());
    }

    @Test
    void testMaxHttpHeaderSize() {
        // default
        assertEquals(8192, protocol.getMaxHttpHeaderSize());
        // changed
        environmentManager.setProperty("server.max-http-header-size", "5120");
        assertEquals(5120, protocol.getMaxHttpHeaderSize());
    }

    @Test
    void testMaxSwallowSize() {
        // default
        assertEquals(1024 * 1024 * 2, protocol.getMaxSwallowSize());
        // changed
        environmentManager.setProperty("server.tomcat.max-swallow-size", "5120");
        assertEquals(5120, protocol.getMaxSwallowSize());
    }

    @Test
    void testMaxHttpFormPostSize() {
        // default
        assertEquals(1024 * 1024 * 2, connector.getMaxPostSize());
        // changed
        environmentManager.setProperty("server.tomcat.max-http-form-post-size", "10240");
        assertEquals(10240, connector.getMaxPostSize());
    }
}
