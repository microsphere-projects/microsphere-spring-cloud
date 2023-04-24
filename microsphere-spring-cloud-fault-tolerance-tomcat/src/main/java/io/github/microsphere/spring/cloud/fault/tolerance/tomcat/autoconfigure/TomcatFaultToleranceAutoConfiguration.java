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
package io.github.microsphere.spring.cloud.fault.tolerance.tomcat.autoconfigure;

import io.github.microsphere.spring.cloud.fault.tolerance.constants.FaultTolerancePropertyConstants;
import io.github.microsphere.spring.cloud.fault.tolerance.tomcat.event.TomcatDynamicConfigurationListener;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.web.context.ConfigurableWebServerApplicationContext;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;

import static io.github.microsphere.spring.cloud.fault.tolerance.constants.FaultTolerancePropertyConstants.ENABLED_PROPERTY_NAME;
import static io.github.microsphere.spring.cloud.fault.tolerance.tomcat.autoconfigure.TomcatFaultToleranceAutoConfiguration.TOMCAT_PROPERTY_PREFIX;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@ConditionalOnProperty(
        prefix = TOMCAT_PROPERTY_PREFIX,
        name = ENABLED_PROPERTY_NAME,
        matchIfMissing = true
)
@ConditionalOnClass(value = {
        EnvironmentChangeEvent.class,
        Tomcat.class
})
@ConditionalOnWebApplication(type = SERVLET)
@AutoConfigureAfter(value = {
        EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
        ConfigurationPropertiesRebinderAutoConfiguration.class
})
public class TomcatFaultToleranceAutoConfiguration {

    public static final String TOMCAT_PROPERTY_PREFIX = FaultTolerancePropertyConstants.PROPERTY_NAME_PREFIX + "tomcat";

    @EventListener(WebServerInitializedEvent.class)
    public void onWebServerInitializedEvent(WebServerInitializedEvent event) {
        WebServerApplicationContext webServerApplicationContext = event.getApplicationContext();
        if (webServerApplicationContext instanceof ConfigurableWebServerApplicationContext) {
            ConfigurableWebServerApplicationContext context = (ConfigurableWebServerApplicationContext) webServerApplicationContext;
            ObjectProvider<ServerProperties> beanProvider = context.getBeanProvider(ServerProperties.class);

            beanProvider.ifAvailable(serverProperties -> {
                WebServer webServer = event.getWebServer();
                if (webServer instanceof TomcatWebServer) {
                    TomcatWebServer tomcatWebServer = (TomcatWebServer) webServer;
                    context.addApplicationListener(new TomcatDynamicConfigurationListener(tomcatWebServer, serverProperties, context));
                }
            });
        }
    }
}
