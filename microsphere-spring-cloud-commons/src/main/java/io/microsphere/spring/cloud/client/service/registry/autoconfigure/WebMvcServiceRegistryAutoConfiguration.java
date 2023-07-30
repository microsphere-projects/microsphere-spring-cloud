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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    private static final Logger logger = LoggerFactory.getLogger(WebMvcServiceRegistryAutoConfiguration.class);

    private static final List<String> DEFAULT_FILTER_URL_MAPPINGS = Arrays.asList("/*");

    @Autowired
    private ObjectProvider<Registration> registrationProvider;

    @Value("${management.endpoints.web.base-path:/actuator}")
    private String actuatorBasePath;

    @Autowired
    private ObjectProvider<WebMvcProperties> webMvcPropertiesProvider;

    @Autowired
    private ObjectProvider<FilterRegistrationBean> filterRegistrationBeansProvider;

    @EventListener(WebEndpointMappingsReadyEvent.class)
    public void onApplicationEvent(WebEndpointMappingsReadyEvent event) {
        registrationProvider.ifAvailable(registration -> {
            Collection<WebEndpointMapping> webEndpointMappings = event.getMappings();
            attachWebMappingsMetadata(registration, webEndpointMappings);
        });
    }

    private void attachWebMappingsMetadata(Registration registration, Collection<WebEndpointMapping> webEndpointMappings) {
        Set<WebEndpointMapping> mappings = new HashSet<>(webEndpointMappings);
        excludeMappings(mappings);
        Map<String, String> metadata = registration.getMetadata();
        StringJoiner jsonBuilder = new StringJoiner(",", "[", "]");
        mappings.stream().map(WebEndpointMapping::toJSON).forEach(jsonBuilder::add);
        String json = jsonBuilder.toString();
        metadata.put(WEB_MAPPINGS_METADATA_NAME, json);
    }

    private void excludeMappings(Set<WebEndpointMapping> mappings) {
        Iterator<WebEndpointMapping> iterator = mappings.iterator();
        while (iterator.hasNext()) {
            WebEndpointMapping mapping = iterator.next();
            String[] patterns = mapping.getPatterns();
            if (isBuiltInFilterMapping(mapping, patterns)) {
                iterator.remove();
                continue;
            }
            for (String pattern : patterns) {
                if (isActuatorWebEndpointMapping(pattern)
                        || isDispatcherServletMapping(pattern)
                ) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    private boolean isActuatorWebEndpointMapping(String pattern) {
        return pattern.startsWith(actuatorBasePath);
    }

    private boolean isDispatcherServletMapping(String pattern) {
        WebMvcProperties webMvcProperties = webMvcPropertiesProvider.getIfAvailable();
        if (webMvcProperties != null) {
            String path = webMvcProperties.getServlet().getPath();
            return Objects.equals(pattern, path);
        }
        return false;
    }

    private boolean isBuiltInFilterMapping(WebEndpointMapping mapping, String[] patterns) {
        Collection<String> patternsSet = Arrays.asList(patterns);

        boolean found = filterRegistrationBeansProvider.stream()
                .filter(filterRegistrationBean -> matchFilter(filterRegistrationBean, patternsSet))
                .filter(filterRegistrationBean -> {
                    Filter filter = filterRegistrationBean.getFilter();
                    Class<? extends Filter> filterClass = filter.getClass();
                    String filterClassName = filterClass.getName();
                    return filterClassName.startsWith("org.springframework.");
                })
                .findFirst()
                .isPresent();

        if (found) {
            Object source = mapping.getSource();
            logger.debug("The build-in filter[name : '{}' , url patterns : '{}'] was matched", source, patternsSet);
        }

        return found;
    }

    private boolean matchFilter(FilterRegistrationBean filterRegistrationBean, Collection<String> patterns) {
        Collection<String> urlPatterns = filterRegistrationBean.getUrlPatterns();
        if (urlPatterns.isEmpty()) {
            urlPatterns = DEFAULT_FILTER_URL_MAPPINGS;
        }
        return urlPatterns.equals(patterns);
    }

}
