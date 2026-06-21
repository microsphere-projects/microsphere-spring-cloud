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

package io.microsphere.spring.cloud.client.actuator;


import org.junit.jupiter.api.Test;

import static io.microsphere.spring.cloud.client.actuator.FeaturesUtils.getAbstractFeaturePropertyName;
import static io.microsphere.spring.cloud.client.actuator.FeaturesUtils.getHasFeaturesBeanName;
import static io.microsphere.spring.cloud.client.actuator.FeaturesUtils.getNamedFeaturePropertyName;
import static io.microsphere.spring.cloud.client.actuator.FeaturesUtils.getQualifierFeatureName;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link FeaturesUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see FeaturesUtils
 * @since 1.0.0
 */
class FeaturesUtilsTest {

    @Test
    void testGetAbstractFeaturePropertyName() {
        String propertyName = getAbstractFeaturePropertyName("jdbc");
        assertEquals("microsphere.spring.cloud.features.abstract.jdbc", propertyName);
    }

    @Test
    void testGetNamedFeaturePropertyName() {
        String propertyName = getNamedFeaturePropertyName("jdbc", "JdbcTemplate");
        assertEquals("microsphere.spring.cloud.features.named.jdbc.JdbcTemplate", propertyName);
    }

    @Test
    void testGetHasFeaturesBeanName() {
        String beanName = getHasFeaturesBeanName("jdbc");
        assertEquals("jdbc.features", beanName);
    }

    @Test
    void testGetQualifierFeatureName() {
        String featureName = getQualifierFeatureName("web", "rest-template");
        assertEquals("microsphere.web.rest-template", featureName);
    }
}