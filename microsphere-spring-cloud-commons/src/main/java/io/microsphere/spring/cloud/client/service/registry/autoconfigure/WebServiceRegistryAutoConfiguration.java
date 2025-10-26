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
package io.microsphere.spring.cloud.client.service.registry.autoconfigure;

import io.microsphere.logging.Logger;
import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnAutoServiceRegistrationEnabled;
import io.microsphere.spring.web.event.WebEndpointMappingsReadyEvent;
import io.microsphere.spring.web.metadata.WebEndpointMapping;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.attachMetadata;

/**
 * Auto-Configuration class for {@link ServiceRegistry ServiceRegistry} on the Spring WebMVC Application
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = {
        "io.microsphere.spring.web.metadata.WebEndpointMapping",
        "io.microsphere.spring.web.event.WebEndpointMappingsReadyEvent"
})
@ConditionalOnBean(Registration.class)
@ConditionalOnAutoServiceRegistrationEnabled
@AutoConfigureAfter(value = {
        ServiceRegistryAutoConfiguration.class
})
public abstract class WebServiceRegistryAutoConfiguration implements ApplicationListener<WebEndpointMappingsReadyEvent> {

    protected final Logger logger = getLogger(getClass());

    @Value("${management.endpoints.web.base-path:/actuator}")
    protected String actuatorBasePath;

    @Override
    public final void onApplicationEvent(WebEndpointMappingsReadyEvent event) {
        ApplicationContext context = event.getApplicationContext();
        ObjectProvider<Registration> registrationProvider = context.getBeanProvider(Registration.class);
        Collection<WebEndpointMapping> webEndpointMappings = event.getMappings();
        registrationProvider.forEach(registration -> attachWebMappingsMetadata(registration, webEndpointMappings));
    }

    private void attachWebMappingsMetadata(Registration registration, Collection<WebEndpointMapping> webEndpointMappings) {
        Set<WebEndpointMapping> mappings = new HashSet<>(webEndpointMappings);
        excludeMappings(mappings);
        attachMetadata(getContextPath(), registration, mappings);
    }

    private void excludeMappings(Set<WebEndpointMapping> mappings) {
        Iterator<WebEndpointMapping> iterator = mappings.iterator();
        while (iterator.hasNext()) {
            WebEndpointMapping mapping = iterator.next();
            String[] patterns = mapping.getPatterns();
            if (isExcludedMapping(mapping, patterns) || isActuatorWebEndpointMapping(mapping, patterns)) {
                logger.trace("The '{}' was excluded", mapping);
                iterator.remove();
            }
        }
    }

    /**
     * Get the context path of the Spring Web Application
     *
     * @return context path
     */
    protected abstract String getContextPath();

    /**
     * Is excluded mapping
     *
     * @param mapping  {@link WebEndpointMapping}
     * @param patterns patterns
     * @return if excluded mapping, return <code>true</code>, or <code>false</code>
     */
    protected abstract boolean isExcludedMapping(WebEndpointMapping mapping, String[] patterns);

    /**
     * Is actuator {@link WebEndpointMapping}
     *
     * @param mapping  {@link WebEndpointMapping}
     * @param patterns patterns
     * @return if actuator {@link WebEndpointMapping}, return <code>true</code>, or <code>false</code>
     */
    protected boolean isActuatorWebEndpointMapping(WebEndpointMapping mapping, String[] patterns) {
        for (String pattern : patterns) {
            if (pattern.startsWith(actuatorBasePath)) {
                return true;
            }
        }
        return false;
    }
}