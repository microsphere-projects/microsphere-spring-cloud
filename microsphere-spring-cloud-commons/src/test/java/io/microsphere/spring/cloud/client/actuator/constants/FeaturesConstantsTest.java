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

package io.microsphere.spring.cloud.client.actuator.constants;


import org.junit.jupiter.api.Test;

import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.ABSTRACT;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.ABSTRACT_FEATURE_PROPERTY_NAME_PATTERN;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.ABSTRACT_FEATURE_PROPERTY_NAME_PREFIX;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.BEAN_NAME_SUFFIX;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.FEATURES;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.FEATURES_ENDPOINT_CLASS_NAME;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.NAMED;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.NAMED_FEATURE_PROPERTY_NAME_PATTERN;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.NAMED_FEATURE_PROPERTY_NAME_PREFIX;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.PLACEHOLDER;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.PROPERTY_NAME_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link FeaturesConstants} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see FeaturesConstants
 * @since 1.0.0
 */
class FeaturesConstantsTest {

    @Test
    void testConstants() {
        assertEquals("features", FEATURES);
        assertEquals("abstract", ABSTRACT);
        assertEquals("named", NAMED);
        assertEquals("{}", PLACEHOLDER);

        assertEquals("microsphere.spring.cloud.features", PROPERTY_NAME_PREFIX);
        assertEquals("microsphere.spring.cloud.features.abstract.", ABSTRACT_FEATURE_PROPERTY_NAME_PREFIX);
        assertEquals("microsphere.spring.cloud.features.named.", NAMED_FEATURE_PROPERTY_NAME_PREFIX);
        assertEquals("microsphere.spring.cloud.features.abstract.{}", ABSTRACT_FEATURE_PROPERTY_NAME_PATTERN);
        assertEquals("microsphere.spring.cloud.features.named.{}.{}", NAMED_FEATURE_PROPERTY_NAME_PATTERN);
        assertEquals(".features", BEAN_NAME_SUFFIX);
        assertEquals("org.springframework.cloud.client.actuator.FeaturesEndpoint", FEATURES_ENDPOINT_CLASS_NAME);
    }
}