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

import io.microsphere.spring.cloud.client.service.registry.condition.ConditionalOnAutoServiceRegistrationEnabled;
import io.microsphere.spring.web.metadata.WebEndpointMapping;
import io.microsphere.spring.webmvc.metadata.WebEndpointMappingsReadyEvent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;

import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_MAPPINGS_METADATA_NAME;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * Auto-Configuration class for {@link ServiceRegistry ServiceRegistry} on the Spring WebMVC Application
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = SERVLET)
@ConditionalOnAutoServiceRegistrationEnabled
@AutoConfigureAfter(value = {
        ServiceRegistryAutoConfiguration.class
})
public class WebMvcServiceRegistryAutoConfiguration {

    @Autowired
    private ObjectProvider<Registration> registrationProvider;

    @EventListener(WebEndpointMappingsReadyEvent.class)
    public void onApplicationEvent(WebEndpointMappingsReadyEvent event) {
        registrationProvider.ifAvailable(registration -> {
            Collection<WebEndpointMapping> webEndpointMappings = event.getMappings();
            attachWebMappingsMetadata(registration, webEndpointMappings);
        });
    }

    private void attachWebMappingsMetadata(Registration registration, Collection<WebEndpointMapping> webEndpointMappings) {
        // TODO remove the duplicated mappings
        Map<String, String> metadata = registration.getMetadata();
        StringJoiner jsonBuilder = new StringJoiner(",", "[", "]");
        webEndpointMappings.stream().map(WebEndpointMapping::toJSON).forEach(jsonBuilder::add);
        String json = jsonBuilder.toString();
        metadata.put(WEB_MAPPINGS_METADATA_NAME, json);
    }

}
