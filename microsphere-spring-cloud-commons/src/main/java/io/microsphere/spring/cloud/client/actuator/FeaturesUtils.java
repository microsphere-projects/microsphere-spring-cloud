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

import io.microsphere.util.Utils;
import org.springframework.cloud.client.actuator.HasFeatures;

import static io.microsphere.constants.PropertyConstants.MICROSPHERE_PROPERTY_NAME_PREFIX;
import static io.microsphere.constants.SymbolConstants.COLON_CHAR;
import static io.microsphere.constants.SymbolConstants.DOT_CHAR;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.ABSTRACT_FEATURE_PROPERTY_NAME_PATTERN;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.BEAN_NAME_SUFFIX;
import static io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants.NAMED_FEATURE_PROPERTY_NAME_PATTERN;
import static io.microsphere.text.FormatUtils.format;

/**
 * The {@link Utils utilities} class for Spring Cloud {@link HasFeatures features}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HasFeatures
 * @see Utils
 * @since 1.0.0
 */
public abstract class FeaturesUtils implements Utils {

    /**
     * Gets the configuration property name for abstract features of the specified module.
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // For module name "jdbc"
     * String propertyName = getAbstractFeaturePropertyName("jdbc");
     * // Result: "microsphere.spring.cloud.features.abstract.jdbc"
     * }</pre>
     *
     * @param moduleName the name of the module
     * @return the configuration property name for abstract features
     */
    public static String getAbstractFeaturePropertyName(String moduleName) {
        return format(ABSTRACT_FEATURE_PROPERTY_NAME_PATTERN, moduleName);
    }

    /**
     * Gets the configuration property name for a named feature of the specified module.
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // For module name "web" and feature name "rest-template"
     * String propertyName = getNamedFeaturePropertyName("web", "rest-template");
     * // Result: "microsphere.spring.cloud.features.named.web.rest-template"
     * }</pre>
     *
     * @param moduleName  the name of the module
     * @param featureName the name of the feature
     * @return the configuration property name for the named feature
     */
    public static String getNamedFeaturePropertyName(String moduleName, String featureName) {
        return format(NAMED_FEATURE_PROPERTY_NAME_PATTERN, moduleName, featureName);
    }

    /**
     * Gets the bean name for {@link HasFeatures} of the specified module.
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // For module name "jdbc"
     * String beanName = getHasFeaturesBeanName("jdbc");
     * // Result: "jdbc.features"
     * }</pre>
     *
     * @param moduleName the name of the module
     * @return the bean name for {@link HasFeatures}
     */
    public static String getHasFeaturesBeanName(String moduleName) {
        return moduleName + BEAN_NAME_SUFFIX;
    }

    /**
     * Gets the qualified feature name for a named feature of the specified module.
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // For module name "web" and feature name "rest-template"
     * String qualifiedName = getQualifierFeatureName("web", "rest-template");
     * // Result: "web:rest-template"
     * }</pre>
     *
     * @param moduleName  the name of the module
     * @param featureName the name of the feature
     * @return the qualified feature name
     */
    public static String getQualifierFeatureName(String moduleName, String featureName) {
        return moduleName + COLON_CHAR + featureName;
    }

    private FeaturesUtils() {
    }
}
