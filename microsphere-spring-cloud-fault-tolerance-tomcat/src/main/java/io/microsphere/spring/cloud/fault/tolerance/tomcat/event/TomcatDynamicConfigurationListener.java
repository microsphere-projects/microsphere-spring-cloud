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

import io.microsphere.spring.util.PropertySourcesUtils;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.microsphere.spring.boot.context.properties.bind.util.BindUtils.bind;
import static io.microsphere.spring.util.EnvironmentUtils.getProperties;
import static io.microsphere.util.Configurer.configure;

/**
 * An {@link ApplicationListener} of {@link EnvironmentChangeEvent} to change
 * the Tomcat's configuration dynamically at the runtime.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class TomcatDynamicConfigurationListener implements ApplicationListener<EnvironmentChangeEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TomcatDynamicConfigurationListener.class);

    private static final String SERVER_PROPERTIES_PREFIX = "server";

    private final TomcatWebServer tomcatWebServer;

    private final ServerProperties serverProperties;

    private final ConfigurableApplicationContext context;

    private final ConfigurableEnvironment environment;

    private final boolean configurationPropertiesRebinderPresent;

    private volatile ServerProperties currentServerProperties;

    public TomcatDynamicConfigurationListener(TomcatWebServer tomcatWebServer, ServerProperties serverProperties,
                                              ConfigurableApplicationContext context) {
        this.tomcatWebServer = tomcatWebServer;
        this.serverProperties = serverProperties;
        ConfigurableEnvironment environment = context.getEnvironment();

        this.context = context;
        this.environment = environment;
        this.configurationPropertiesRebinderPresent = isBeanPresent(ConfigurationPropertiesRebinder.class);

        initCurrentServerProperties();
    }

    private void initCurrentServerProperties() {
        this.currentServerProperties = getCurrentServerProperties(environment);
    }

    private boolean isBeanPresent(Class<?> beanType) {
        return context.getBeanProvider(ConfigurationPropertiesRebinder.class).getIfAvailable() != null;
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        if (!isSourceFrom(event)) {
            logger.debug("Current context[id : '{}'] receives the other changed property names : {}", context.getId(), event.getKeys());
            return;
        }

        Set<String> serverPropertyNames = filterServerPropertyNames(event);
        if (serverPropertyNames.isEmpty()) {
            logger.debug("Current context[id : '{}'] does not receive the property change of ServerProperties, keys : {}", context.getId(), event.getKeys());
            return;
        }

        configureTomcatIfChanged(serverPropertyNames);
    }

    private ServerProperties getCurrentServerProperties(ConfigurableEnvironment environment) {
        Map<String, Object> properties = PropertySourcesUtils.getSubProperties(environment, SERVER_PROPERTIES_PREFIX);
        return bind(properties, "", ServerProperties.class);
    }

    private boolean isSourceFrom(EnvironmentChangeEvent event) {
        return context.equals(event.getSource());
    }

    private Set<String> filterServerPropertyNames(EnvironmentChangeEvent event) {
        return event.getKeys().stream().filter(this::isServerPropertyName).collect(Collectors.toSet());
    }

    private boolean isServerPropertyName(String propertyName) {
        return propertyName.startsWith(SERVER_PROPERTIES_PREFIX);
    }

    private void configureTomcatIfChanged(Set<String> serverPropertyNames) {
        ServerProperties refreshableServerProperties = getRefreshableServerProperties(serverPropertyNames);
        logger.debug("The ServerProperties property is changed to: {}", getProperties(environment, serverPropertyNames));
        configureConnector(refreshableServerProperties);
        // Reset current ServerProperties
        initCurrentServerProperties();
    }

    private ServerProperties getRefreshableServerProperties(Set<String> serverPropertyNames) {
        if (configurationPropertiesRebinderPresent) { // Refresh
            return serverProperties;
        } else {
            return bindServerProperties(serverPropertyNames);
        }
    }

    private ServerProperties bindServerProperties(Set<String> serverPropertyNames) {
        Map<String, String> serverProperties = getProperties(environment, serverPropertyNames);
        return bind(serverProperties, SERVER_PROPERTIES_PREFIX, ServerProperties.class);
    }

    private void configureConnector(ServerProperties refreshableServerProperties) {
        Connector connector = tomcatWebServer.getTomcat().getConnector();
        ProtocolHandler protocolHandler = connector.getProtocolHandler();
        configureProtocol(refreshableServerProperties, protocolHandler);
        configureHttp11Protocol(refreshableServerProperties, connector, protocolHandler);
    }

    private void configureProtocol(ServerProperties refreshableServerProperties, ProtocolHandler protocolHandler) {
        if (protocolHandler instanceof AbstractProtocol) {

            ServerProperties.Tomcat refreshableTomcatProperties = refreshableServerProperties.getTomcat();

            ServerProperties currentServerProperties = this.currentServerProperties;
            ServerProperties.Tomcat currentTomcatProperties = currentServerProperties.getTomcat();

            AbstractProtocol protocol = (AbstractProtocol) protocolHandler;

            // Thread Pool code size
            configure("Tomcat thread pool's core size")
                    .value(refreshableTomcatProperties.getThreads()::getMinSpare)
                    .on(this::isPositive)
                    .compare(currentTomcatProperties.getThreads()::getMinSpare)
                    .apply(protocol::setMinSpareThreads);

            // Thread Pool max size
            configure("Tomcat thread pool's max size")
                    .value(refreshableTomcatProperties.getThreads()::getMax)
                    .on(this::isPositive)
                    .compare(currentTomcatProperties.getThreads()::getMax)
                    .apply(protocol::setMaxThreads);

            // Network backlog
            configure("Tomcat Endpoint Network backlog")
                    .value(refreshableTomcatProperties::getAcceptCount)
                    .on(this::isPositive)
                    .compare(currentTomcatProperties::getAcceptCount)
                    .apply(protocol::setAcceptCount);

            // Network connection timeout
            configure("Tomcat connection timeout(ms)")
                    .value(refreshableTomcatProperties::getConnectionTimeout)
                    .compare(currentTomcatProperties::getConnectionTimeout)
                    .as(this::toIntMillis)
                    .on(this::isPositive)
                    .apply(protocol::setConnectionTimeout);

            // Max connections
            configure("Tomcat max connections")
                    .value(refreshableTomcatProperties::getMaxConnections)
                    .on(this::isPositive)
                    .compare(currentTomcatProperties::getMaxConnections)
                    .apply(protocol::setMaxConnections);
        }
    }

    private void configureHttp11Protocol(ServerProperties refreshableServerProperties, Connector connector, ProtocolHandler protocolHandler) {
        if (protocolHandler instanceof AbstractHttp11Protocol) {
            AbstractHttp11Protocol protocol = (AbstractHttp11Protocol) protocolHandler;

            ServerProperties.Tomcat refreshableTomcatProperties = refreshableServerProperties.getTomcat();
            ServerProperties currentServerProperties = this.currentServerProperties;
            ServerProperties.Tomcat currentTomcatProperties = currentServerProperties.getTomcat();

            // Max HTTP Header Size
            configure("Tomcat HTTP Headers' max size(bytes)")
                    .value(refreshableServerProperties::getMaxHttpHeaderSize)
                    .compare(currentServerProperties::getMaxHttpHeaderSize)
                    .as(this::toIntBytes)
                    .on(this::isPositive)
                    .apply(protocol::setMaxHttpHeaderSize);

            // Max Swallow Size
            configure("Tomcat HTTP Body's max size(bytes)")
                    .value(refreshableTomcatProperties::getMaxSwallowSize)
                    .compare(currentTomcatProperties::getMaxSwallowSize)
                    .as(this::toIntBytes)
                    .on(this::isPositive)
                    .apply(protocol::setMaxSwallowSize);

            // Max Post Size
            configure("Tomcat HTTP max POST size(bytes)")
                    .value(refreshableTomcatProperties::getMaxHttpFormPostSize)
                    .compare(currentTomcatProperties::getMaxHttpFormPostSize)
                    .as(this::toIntBytes)
                    .on(this::isPositive)
                    .apply(connector::setMaxPostSize);
        }
    }

    private int toIntMillis(Duration duration) {
        return (int) duration.toMillis();
    }

    private int toIntBytes(DataSize dataSize) {
        return (int) dataSize.toBytes();
    }

    private boolean isPositive(int value) {
        return value > 0;
    }


}
