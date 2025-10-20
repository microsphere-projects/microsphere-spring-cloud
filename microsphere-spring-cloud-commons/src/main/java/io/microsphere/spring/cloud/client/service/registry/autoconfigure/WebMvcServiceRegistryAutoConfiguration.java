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
import io.microsphere.util.ValueHolder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.attachMetadata;
import static io.microsphere.util.ArrayUtils.EMPTY_STRING_ARRAY;
import static io.microsphere.util.ArrayUtils.arrayEquals;
import static java.lang.Boolean.FALSE;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

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
@ConditionalOnWebApplication(type = SERVLET)
@ConditionalOnAutoServiceRegistrationEnabled
@AutoConfigureAfter(value = {
        ServiceRegistryAutoConfiguration.class
})
public class WebMvcServiceRegistryAutoConfiguration {

    private static final Logger logger = getLogger(WebMvcServiceRegistryAutoConfiguration.class);

    private static final String[] DEFAULT_URL_MAPPINGS = {"/*"};

    @Autowired
    private Registration registration;

    @Value("${management.endpoints.web.base-path:/actuator}")
    private String actuatorBasePath;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Autowired
    private ObjectProvider<FilterRegistrationBean> filterRegistrationBeansProvider;

    @Autowired
    private ObjectProvider<DispatcherServletRegistrationBean> dispatcherServletRegistrationBeanProvider;

    @EventListener(WebEndpointMappingsReadyEvent.class)
    public void onApplicationEvent(WebEndpointMappingsReadyEvent event) {
        Collection<WebEndpointMapping> webEndpointMappings = event.getMappings();
        attachWebMappingsMetadata(registration, webEndpointMappings);
    }

    private void attachWebMappingsMetadata(Registration registration, Collection<WebEndpointMapping> webEndpointMappings) {
        Set<WebEndpointMapping> mappings = new HashSet<>(webEndpointMappings);
        excludeMappings(mappings);
        attachMetadata(contextPath, registration, mappings);
    }

    private void excludeMappings(Set<WebEndpointMapping> mappings) {
        Iterator<WebEndpointMapping> iterator = mappings.iterator();
        while (iterator.hasNext()) {
            WebEndpointMapping mapping = iterator.next();
            String[] patterns = mapping.getPatterns();
            if (isBuiltInFilterMapping(patterns)
                    || isDispatcherServletMapping(mapping, patterns)
                    || isActuatorWebEndpointMapping(patterns)
            ) {
                logger.trace("The '{}' was removed", mapping);
                iterator.remove();
            }

        }
    }

    private boolean isBuiltInFilterMapping(String[] patterns) {
        boolean found = filterRegistrationBeansProvider.stream()
                .filter(filterRegistrationBean -> matchFilter(filterRegistrationBean, patterns))
                .filter(filterRegistrationBean -> {
                    Filter filter = filterRegistrationBean.getFilter();
                    Class<? extends Filter> filterClass = filter.getClass();
                    String filterClassName = filterClass.getName();
                    return filterClassName.startsWith("org.springframework.");
                })
                .findFirst()
                .isPresent();

        return found;
    }

    private boolean isDispatcherServletMapping(WebEndpointMapping mapping, String[] patterns) {
        ValueHolder<Boolean> found = new ValueHolder<>(FALSE);
        this.dispatcherServletRegistrationBeanProvider.ifAvailable(registrationBean -> {
            Object source = mapping.getEndpoint();
            String servletName = registrationBean.getServletName();
            if (Objects.equals(source, servletName)) {
                Collection<String> urlMappings = registrationBean.getUrlMappings();
                found.setValue(matchUrlPatterns(urlMappings, patterns));
            }
        });
        return found.getValue();
    }


    private boolean isActuatorWebEndpointMapping(String[] patterns) {
        for (String pattern : patterns) {
            if (pattern.startsWith(actuatorBasePath)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchFilter(FilterRegistrationBean filterRegistrationBean, String[] patterns) {
        Collection<String> urlPatterns = filterRegistrationBean.getUrlPatterns();
        return matchUrlPatterns(urlPatterns, patterns);
    }

    private boolean matchUrlPatterns(Collection<String> urlPatterns, String[] patterns) {
        String[] urlPatternsArray = urlPatterns.isEmpty() ? DEFAULT_URL_MAPPINGS : urlPatterns.toArray(EMPTY_STRING_ARRAY);
        return arrayEquals(urlPatternsArray, patterns);
    }

}
