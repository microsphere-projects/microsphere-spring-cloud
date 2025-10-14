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
package io.microsphere.spring.cloud.commons.constants;

import io.microsphere.annotation.ConfigurationProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration;

import static io.microsphere.annotation.ConfigurationProperty.APPLICATION_SOURCE;
import static io.microsphere.constants.PropertyConstants.ENABLED_PROPERTY_NAME;
import static io.microsphere.constants.PropertyConstants.MICROSPHERE_PROPERTY_NAME_PREFIX;

/**
 * The property constants for Spring Cloud Commons
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface CommonsPropertyConstants {

    /**
     * The property name prefix of Spring Cloud properties : "spring.cloud."
     */
    String SPRING_CLOUD_PROPERTY_PREFIX = "spring.cloud.";

    /**
     * The property name prefix of Spring Cloud Service Registry : "spring.cloud.service-registry."
     */
    String SERVICE_REGISTRY_PROPERTY_PREFIX = SPRING_CLOUD_PROPERTY_PREFIX + "service-registry.";

    /**
     * The property name for Spring Cloud Service Registry Auto-Registration Feature :
     * "spring.cloud.service-registry.auto-registration.enabled"
     *
     * @see AutoServiceRegistrationAutoConfiguration
     */
    String SERVICE_REGISTRY_AUTO_REGISTRATION_ENABLED_PROPERTY_NAME = SERVICE_REGISTRY_PROPERTY_PREFIX + "auto-registration." + ENABLED_PROPERTY_NAME;

    /**
     * The property name for enabling Spring Cloud Features : "spring.cloud.features.enabled"
     *
     * @see CommonsClientAutoConfiguration.ActuatorConfiguration
     */
    String FEATURES_ENABLED_PROPERTY_NAME = SPRING_CLOUD_PROPERTY_PREFIX + "features." + ENABLED_PROPERTY_NAME;

    /**
     * The property name prefix of Microsphere Cloud : "microsphere.spring.cloud."
     */
    String MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX = MICROSPHERE_PROPERTY_NAME_PREFIX + SPRING_CLOUD_PROPERTY_PREFIX;

    /**
     * The property name prefix of Microsphere Cloud Web MVC : "microsphere.spring.cloud.web.mvc."
     */
    String MICROSPHERE_SPRING_CLOUD_WEB_MVC_PROPERTY_NAME_PREFIX = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "web.mvc.";

    /**
     * The property name for Multiple Service Registry Enabled Feature : "microsphere.spring.cloud.multiple-registration.enabled"
     */
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = "false",
            source = APPLICATION_SOURCE
    )
    String MULTIPLE_REGISTRATION_ENABLED_PROPERTY_NAME = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "multiple-registration." + ENABLED_PROPERTY_NAME;

    /**
     * The property name for Default Service Registry Type : "microsphere.spring.cloud.default-registration.type"
     */
    @ConfigurationProperty(
            type = Class.class,
            source = APPLICATION_SOURCE
    )
    String MULTIPLE_REGISTRATION_DEFAULT_REGISTRATION_PROPERTY_NAME = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "default-registration.type";

    /**
     * The property name for Default Service Registry Type : "microsphere.spring.cloud.default-service-registry.type"
     */
    @ConfigurationProperty(
            type = Class.class,
            source = APPLICATION_SOURCE
    )
    String MULTIPLE_REGISTRATION_DEFAULT_REGISTRY_PROPERTY_NAME = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "default-service-registry.type";

    /**
     * The property name for Composite Service Registry Enabled Feature : "microsphere.spring.cloud.composite-registration.enabled"
     */
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = "false",
            source = APPLICATION_SOURCE
    )
    String COMPOSITE_REGISTRATION_ENABLED_PROPERTY_NAME = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "composite-registration." + ENABLED_PROPERTY_NAME;

}
