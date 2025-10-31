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


import org.junit.jupiter.api.Test;

import static io.microsphere.spring.cloud.commons.constants.SpringCloudPropertyConstants.FEATURES_ENABLED_PROPERTY_NAME;
import static io.microsphere.spring.cloud.commons.constants.SpringCloudPropertyConstants.LOAD_BALANCER_ENABLED_PROPERTY_NAME;
import static io.microsphere.spring.cloud.commons.constants.SpringCloudPropertyConstants.SERVICE_REGISTRY_AUTO_REGISTRATION_ENABLED_PROPERTY_NAME;
import static io.microsphere.spring.cloud.commons.constants.SpringCloudPropertyConstants.SERVICE_REGISTRY_PROPERTY_PREFIX;
import static io.microsphere.spring.cloud.commons.constants.SpringCloudPropertyConstants.SPRING_CLOUD_PROPERTY_PREFIX;
import static io.microsphere.spring.cloud.commons.constants.SpringCloudPropertyConstants.UTIL_ENABLED_PROPERTY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link SpringCloudPropertyConstants} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SpringCloudPropertyConstants
 * @since 1.0.0
 */
class SpringCloudPropertyConstantsTest {

    @Test
    void testConstants() {
        assertEquals("spring.cloud.", SPRING_CLOUD_PROPERTY_PREFIX);
        assertEquals("spring.cloud.service-registry.", SERVICE_REGISTRY_PROPERTY_PREFIX);
        assertEquals("spring.cloud.service-registry.auto-registration.enabled", SERVICE_REGISTRY_AUTO_REGISTRATION_ENABLED_PROPERTY_NAME);
        assertEquals("spring.cloud.features.enabled", FEATURES_ENABLED_PROPERTY_NAME);
        assertEquals("spring.cloud.loadbalancer.enabled", LOAD_BALANCER_ENABLED_PROPERTY_NAME);
        assertEquals("spring.cloud.util.enabled", UTIL_ENABLED_PROPERTY_NAME);
    }
}