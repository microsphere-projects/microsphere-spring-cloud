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
package io.microsphere.spring.cloud.client.condition;

import io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.actuator.FeaturesEndpoint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.FEATURES_ENDPOINT_CLASS_NAME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link org.springframework.context.annotation.Conditional} that only matches when Spring Cloud features are enabled
 * and the {@link FeaturesEndpoint} is available.
 * <p>
 * This annotation combines {@link ConditionalOnFeaturesEnabled} and {@link ConditionalOnAvailableEndpoint} to ensure
 * that the annotated component is only created if the application has explicitly enabled Spring Cloud features
 * (via the property defined in {@link CommonsPropertyConstants#FEATURES_ENABLED_PROPERTY_NAME}) and the
 * {@link FeaturesEndpoint} actuator endpoint is exposed and accessible.
 *
 * <h3>Example Usage</h3>
 * <pre>{@code
 * @Configuration
 * @ConditionalOnFeaturesAvailable
 * public class MyFeatureConfiguration {
 *
 *     @Bean
 *     public MyService myService() {
 *         return new MyService();
 *     }
 * }
 * }</pre>
 *
 * <p>
 * In the example above, {@code MyFeatureConfiguration} will only be loaded if:
 * <ul>
 *     <li>The property {@code microsphere.spring.cloud.features.enabled} is set to {@code true}.</li>
 *     <li>The {@link FeaturesEndpoint} is available (i.e., included in the management endpoints).</li>
 * </ul>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConditionalOnFeaturesEnabled
 * @see ConditionalOnAvailableEndpoint
 * @see FeaturesEndpoint
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Documented
@ConditionalOnFeaturesEnabled
@ConditionalOnClass(name = {
        FEATURES_ENDPOINT_CLASS_NAME
})
@ConditionalOnAvailableEndpoint(endpoint = FeaturesEndpoint.class)
public @interface ConditionalOnFeaturesAvailable {
}