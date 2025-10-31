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
import org.springframework.cloud.commons.util.UtilAutoConfiguration;

import static io.microsphere.annotation.ConfigurationProperty.APPLICATION_SOURCE;
import static io.microsphere.constants.PropertyConstants.ENABLED_PROPERTY_NAME;

/**
 * The property constants for Spring Cloud
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AutoServiceRegistrationAutoConfiguration
 * @see CommonsClientAutoConfiguration
 * @since 1.0.0
 */
public interface SpringCloudPropertyConstants {

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
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = "true",
            source = APPLICATION_SOURCE
    )
    String SERVICE_REGISTRY_AUTO_REGISTRATION_ENABLED_PROPERTY_NAME = SERVICE_REGISTRY_PROPERTY_PREFIX + "auto-registration." + ENABLED_PROPERTY_NAME;

    /**
     * The property name for enabling Spring Cloud Features : "spring.cloud.features.enabled"
     *
     * @see CommonsClientAutoConfiguration.ActuatorConfiguration
     */
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = "true",
            source = APPLICATION_SOURCE
    )
    String FEATURES_ENABLED_PROPERTY_NAME = SPRING_CLOUD_PROPERTY_PREFIX + "features." + ENABLED_PROPERTY_NAME;

    /**
     * The property name for enabling Spring Cloud Load-Balancer : "spring.cloud.loadbalancer.enabled"
     *
     * @see org.springframework.cloud.loadbalancer.config.LoadBalancerAutoConfiguration
     */
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = "true",
            source = APPLICATION_SOURCE
    )
    String LOAD_BALANCER_ENABLED_PROPERTY_NAME = SPRING_CLOUD_PROPERTY_PREFIX + "loadbalancer." + ENABLED_PROPERTY_NAME;


    /**
     * The property name for enabling Spring Cloud Util : "spring.cloud.util.enabled"
     *
     * @see UtilAutoConfiguration
     */
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = "true",
            source = APPLICATION_SOURCE
    )
    String UTIL_ENABLED_PROPERTY_NAME = SPRING_CLOUD_PROPERTY_PREFIX + "util." + ENABLED_PROPERTY_NAME;
}