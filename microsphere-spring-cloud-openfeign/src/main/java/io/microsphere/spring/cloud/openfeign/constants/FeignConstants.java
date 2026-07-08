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

package io.microsphere.spring.cloud.openfeign.constants;

import io.microsphere.annotation.ConfigurationProperty;
import io.microsphere.constants.PropertyConstants;
import io.microsphere.spring.cloud.openfeign.autoconfigure.FeignAutoConfiguration;

import static io.microsphere.annotation.ConfigurationProperty.APPLICATION_SOURCE;
import static io.microsphere.constants.SymbolConstants.DOT;
import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX;

/**
 * The constants for Open Feign
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see feign.Feign
 * @since 1.0.0
 */
public interface FeignConstants {

    /**
     * The property name for enabling {@link FeignAutoConfiguration} : "microsphere.spring.cloud.openfeign.enabled"
     */
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = "true",
            source = APPLICATION_SOURCE
    )
    String ENABLED_PROPERTY_NAME = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "openfeign" + DOT + PropertyConstants.ENABLED_PROPERTY_NAME;

    /**
     * The class name of {@link feign.Feign}
     *
     * @see feign.Feign
     */
    String FEIGN_CLASS_NAME = "feign.Feign";

    /**
     * The class name of {@link feign.Capability}
     *
     * @see feign.Capability
     */
    String FEIGN_CAPABILITY_CLASS_NAME = "feign.Capability";

    /**
     * The class name of {@link org.springframework.cloud.openfeign.FeignBuilderCustomizer}
     *
     * @see org.springframework.cloud.openfeign.FeignBuilderCustomizer
     */
    String FEIGN_BUILDER_CUSTOMIZER_CLASS_NAME = "org.springframework.cloud.openfeign.FeignBuilderCustomizer";

    /**
     * The class name of {@link org.springframework.cloud.openfeign.FeignClientFactoryBean}
     *
     * @see org.springframework.cloud.openfeign.FeignClientFactoryBean
     */
    String FEIGN_CLIENT_FACTORY_BEAN_CLASS_NAME = "org.springframework.cloud.openfeign.FeignClientFactoryBean";

    /**
     * The class name of {@link org.springframework.cloud.openfeign.FeignAutoConfiguration}
     *
     * @see org.springframework.cloud.openfeign.FeignAutoConfiguration
     */
    String FEIGN_AUTO_CONFIGURATION_CLASS_NAME = "org.springframework.cloud.openfeign.FeignAutoConfiguration";

}