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

import io.microsphere.spring.web.metadata.WebEndpointMapping;
import io.microsphere.util.ValueHolder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import javax.servlet.Filter;
import java.util.Collection;
import java.util.Objects;

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
@ConditionalOnWebApplication(type = SERVLET)
public class WebMvcServiceRegistryAutoConfiguration extends WebServiceRegistryAutoConfiguration {

    private static final String[] DEFAULT_URL_MAPPINGS = {"/*"};

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Autowired
    private ObjectProvider<FilterRegistrationBean> filterRegistrationBeansProvider;

    @Autowired
    private ObjectProvider<DispatcherServletRegistrationBean> dispatcherServletRegistrationBeanProvider;

    @Override
    protected String getContextPath() {
        return this.contextPath;
    }

    @Override
    protected boolean isExcludedMapping(WebEndpointMapping mapping, String[] patterns) {
        return isBuiltInFilterMapping(patterns) || isDispatcherServletMapping(mapping, patterns);
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

    private boolean matchFilter(FilterRegistrationBean filterRegistrationBean, String[] patterns) {
        Collection<String> urlPatterns = filterRegistrationBean.getUrlPatterns();
        return matchUrlPatterns(urlPatterns, patterns);
    }

    private boolean matchUrlPatterns(Collection<String> urlPatterns, String[] patterns) {
        String[] urlPatternsArray = urlPatterns.isEmpty() ? DEFAULT_URL_MAPPINGS : urlPatterns.toArray(EMPTY_STRING_ARRAY);
        return arrayEquals(urlPatternsArray, patterns);
    }
}