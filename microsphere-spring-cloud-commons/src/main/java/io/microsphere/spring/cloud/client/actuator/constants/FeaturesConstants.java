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

import org.springframework.cloud.client.actuator.HasFeatures;

import static io.microsphere.constants.SymbolConstants.DOT;
import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX;

/**
 * The constants class for Spring Cloud {@link HasFeatures features}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HasFeatures
 * @see org.springframework.cloud.client.actuator.FeaturesEndpoint
 * @since 1.0.0
 */
public interface FeaturesConstants {

    String FEATURES = "features";

    String ABSTRACT = "abstract";

    String NAMED = "named";

    String PLACEHOLDER = "{}";

    /**
     * The prefix of the configuration properties for {@link HasFeatures} : "microsphere.spring.cloud.features"
     */
    String PROPERTY_NAME_PREFIX = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + FEATURES;

    /**
     * The prefix of the configuration properties for abstract features: "microsphere.spring.cloud.features.abstract."
     */
    String ABSTRACT_FEATURE_PROPERTY_NAME_PREFIX = PROPERTY_NAME_PREFIX + DOT + ABSTRACT + DOT;

    /**
     * The prefix of the configuration properties for named features: "microsphere.spring.cloud.features.named."
     */
    String NAMED_FEATURE_PROPERTY_NAME_PREFIX = PROPERTY_NAME_PREFIX + DOT + NAMED + DOT;

    /**
     * The pattern of the configuration properties for abstract features: "microsphere.spring.cloud.features.abstract.{module-name}"
     */
    String ABSTRACT_FEATURE_PROPERTY_NAME_PATTERN = ABSTRACT_FEATURE_PROPERTY_NAME_PREFIX + PLACEHOLDER;

    /**
     * The pattern of the configuration properties for named features: "microsphere.spring.cloud.features.named.{module-name}.{feature-name}"
     */
    String NAMED_FEATURE_PROPERTY_NAME_PATTERN = NAMED_FEATURE_PROPERTY_NAME_PREFIX + PLACEHOLDER + DOT + PLACEHOLDER;

    /**
     * The suffix of the bean name for {@link HasFeatures} : ".features"
     */
    String BEAN_NAME_SUFFIX = DOT + FEATURES;
}
