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


import io.microsphere.spring.boot.test.WebAutoConfigurationTest;
import io.microsphere.spring.cloud.client.service.registry.DefaultRegistration;
import io.microsphere.spring.web.method.support.HandlerMethodInterceptor;
import io.microsphere.spring.webmvc.annotation.EnableWebMvcExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Set;

/**
 * {@link WebMvcServiceRegistryAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see WebMvcServiceRegistryAutoConfiguration
 * @since 1.0.0
 */
@SpringBootTest(
        classes = {
                DefaultRegistration.class,
                WebMvcServiceRegistryAutoConfigurationTest.class
        }
)
class WebMvcServiceRegistryAutoConfigurationTest extends WebAutoConfigurationTest<WebMvcServiceRegistryAutoConfiguration> {

    @Override
    protected void configureAutoConfiguredClasses(Set<Class<?>> autoConfiguredClasses) {
    }

    @Override
    protected void configureGlobalDisabledPropertyValues(Set<String> globalDisabledPropertyValues) {
        globalDisabledPropertyValues.add("microsphere.spring.boot.webmvc.enabled=false");
        globalDisabledPropertyValues.add("spring.cloud.service-registry.auto-registration.enabled=false");
    }

    @Override
    protected void configureGlobalMissingClasses(Set<Class<?>> globalMissingClasses) {
        globalMissingClasses.add(HandlerMethod.class);
        globalMissingClasses.add(DispatcherServlet.class);
        globalMissingClasses.add(HandlerMethodInterceptor.class);
        globalMissingClasses.add(EnableWebMvcExtension.class);
        globalMissingClasses.add(Registration.class);
    }
}