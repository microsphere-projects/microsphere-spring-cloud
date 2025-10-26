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

package io.microsphere.spring.cloud.fault.tolerance.tomcat.event;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import static io.microsphere.collection.Sets.ofSet;

/**
 * {@link TomcatDynamicConfigurationListener} Testt
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see TomcatDynamicConfigurationListener
 * @since 1.0.0
 */
class TomcatDynamicConfigurationListenerTest {

    private ServerProperties serverProperties;

    private MockEnvironment environment;

    private ConfigurableApplicationContext context;

    private TomcatDynamicConfigurationListener listener;

    @BeforeEach
    void setUp() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        TomcatWebServer tomcatWebServer = (TomcatWebServer) factory.getWebServer();
        this.serverProperties = new ServerProperties();
        this.environment = new MockEnvironment();
        this.environment.setProperty("server.port", "8080");
        this.context = new GenericApplicationContext();
        this.context.setEnvironment(this.environment);
        this.context.refresh();
        this.listener = new TomcatDynamicConfigurationListener(tomcatWebServer, this.serverProperties, this.context);
    }

    @Test
    void testOnApplicationEventOnDifferentEventSource() {
        EnvironmentChangeEvent event = new EnvironmentChangeEvent(ofSet("test-key"));
        this.listener.onApplicationEvent(event);
    }

    @Test
    void testOnApplicationEventOnEmptyKeys() {
        EnvironmentChangeEvent event = new EnvironmentChangeEvent(this.context, ofSet());
        this.listener.onApplicationEvent(event);
    }

    @Test
    void testOnApplicationEvent() {
        String key = "server.tomcat.threads.max";
        this.environment.setProperty(key, "100");
        EnvironmentChangeEvent event = new EnvironmentChangeEvent(this.context, ofSet(key));
        this.listener.onApplicationEvent(event);
    }

    @Test
    void testConfigureProtocolWithNull() {
        this.listener.configureProtocol(this.serverProperties, null);
    }

    @Test
    void testConfigureHttp11ProtocolWithNull() {
        this.listener.configureHttp11Protocol(this.serverProperties, null, null);
    }

    @Test
    void testIsPositiveWithNegative() {
        this.listener.isPositive(-1);
    }
}