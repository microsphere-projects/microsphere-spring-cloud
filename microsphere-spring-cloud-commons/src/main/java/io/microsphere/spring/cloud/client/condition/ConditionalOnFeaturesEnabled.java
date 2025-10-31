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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.actuator.FeaturesEndpoint;
import org.springframework.cloud.client.actuator.HasFeatures;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.FEATURES_ENABLED_PROPERTY_NAME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The conditional annotation meta-annotates {@link ConditionalOnProperty @ConditionalOnProperty} for
 * {@link FeaturesEndpoint @FeaturesEndpoint} enabled.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CommonsClientAutoConfiguration.ActuatorConfiguration
 * @see FeaturesEndpoint
 * @see HasFeatures
 * @see CommonsPropertyConstants#FEATURES_ENABLED_PROPERTY_NAME
 * @see ConditionalOnProperty
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Documented
@ConditionalOnProperty(name = FEATURES_ENABLED_PROPERTY_NAME, matchIfMissing = true)
public @interface ConditionalOnFeaturesEnabled {
}
