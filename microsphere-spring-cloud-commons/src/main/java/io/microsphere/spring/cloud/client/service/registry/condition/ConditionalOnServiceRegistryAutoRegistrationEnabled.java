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
package io.microsphere.spring.cloud.client.service.registry.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.microsphere.spring.cloud.client.constants.CommonsPropertyConstants.SERVICE_REGISTRY_AUTO_REGISTRATION_ENABLED_PROPERTY_NAME;

/**
 * The conditional annotation meta-annotates
 * {@link ConditionalOnProperty @ConditionalOnProperty} for Service Registry Auto-Registration enabled.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConditionalOnProperty
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ConditionalOnProperty(name = SERVICE_REGISTRY_AUTO_REGISTRATION_ENABLED_PROPERTY_NAME)
public @interface ConditionalOnServiceRegistryAutoRegistrationEnabled {

    /**
     * The string representation of the expected value for the properties. If not
     * specified, the property must <strong>not</strong> be equal to {@code false}.
     *
     * @return the expected value
     */
    @AliasFor(annotation = ConditionalOnProperty.class, attribute = "havingValue")
    String havingValue() default "";

    /**
     * Specify if the condition should match if the property is not set. Defaults to
     * {@code false}.
     *
     * @return if the condition should match if the property is missing
     */
    @AliasFor(annotation = ConditionalOnProperty.class, attribute = "matchIfMissing")
    boolean matchIfMissing() default false;
}
