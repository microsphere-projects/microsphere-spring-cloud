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
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;

/**
 * Auto-Configuration class for {@link ServiceRegistry ServiceRegistry} on the Spring WebFlux Application
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@ConditionalOnWebApplication(type = REACTIVE)
public class WebFluxServiceRegistryAutoConfiguration extends WebServiceRegistryAutoConfiguration {

    /**
     * {@inheritDoc}
     * <p>Returns an empty string as WebFlux applications do not use a servlet context path.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WebFluxServiceRegistryAutoConfiguration config = new WebFluxServiceRegistryAutoConfiguration();
     * String contextPath = config.getContextPath(); // returns ""
     * }</pre>
     *
     * @return an empty string
     */
    @Override
    protected String getContextPath() {
        return "";
    }

    /**
     * {@inheritDoc}
     * <p>Always returns {@code false} for WebFlux applications, as no mappings are excluded.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WebFluxServiceRegistryAutoConfiguration config = new WebFluxServiceRegistryAutoConfiguration();
     * boolean excluded = config.isExcludedMapping(mapping, patterns); // always false
     * }</pre>
     *
     * @param mapping  the {@link WebEndpointMapping} to evaluate
     * @param patterns the URL patterns associated with the mapping
     * @return always {@code false}
     */
    @Override
    protected boolean isExcludedMapping(WebEndpointMapping mapping, String[] patterns) {
        return false;
    }
}