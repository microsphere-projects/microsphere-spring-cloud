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

import io.microsphere.logging.Logger;
import io.microsphere.spring.cloud.client.condition.ConditionalOnFeaturesEnabled;
import io.microsphere.spring.context.config.AutoRegistrationBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.client.actuator.FeaturesEndpoint;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.cloud.client.actuator.NamedFeature;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static io.microsphere.collection.ListUtils.newLinkedList;
import static io.microsphere.collection.MapUtils.newLinkedHashMap;
import static io.microsphere.constants.PropertyConstants.MICROSPHERE_PROPERTY_NAME_PREFIX;
import static io.microsphere.constants.SymbolConstants.COMMA_CHAR;
import static io.microsphere.constants.SymbolConstants.DOT;
import static io.microsphere.constants.SymbolConstants.DOT_CHAR;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX;
import static io.microsphere.spring.core.env.EnvironmentUtils.asConfigurableEnvironment;
import static io.microsphere.spring.core.env.PropertySourcesUtils.getSubProperties;
import static io.microsphere.text.FormatUtils.format;
import static io.microsphere.util.ClassLoaderUtils.resolveClass;
import static io.microsphere.util.StringUtils.split;
import static java.lang.String.valueOf;

/**
 * Auto-registrar for Spring Cloud Client Actuator's {@link HasFeatures} based on configuration properties.
 * <p>
 * This class scans configuration properties under the prefix {@value #PROPERTY_PREFIX} to automatically register
 * {@link HasFeatures} beans. It supports two types of feature definitions:
 * <ul>
 *     <li><strong>Abstract Features:</strong> Defined by listing feature classes directly under a module name.</li>
 *     <li><strong>Named Features:</strong> Defined by mapping a specific feature name to a feature class under a module name.</li>
 * </ul>
 *
 * <h3>Configuration Examples</h3>
 *
 * <h4>1. Abstract Features</h4>
 * <pre>{@code
 * # Defines abstract features for the 'jdbc' module
 * microsphere.spring.cloud.features.jdbc=org.springframework.jdbc.core.JdbcTemplate,org.springframework.transaction.PlatformTransactionManager
 * }</pre>
 *
 * <h4>2. Named Features</h4>
 * <pre>{@code
 * # Defines a named feature 'rest-template' for the 'web' module
 * microsphere.spring.cloud.features.web.rest-template=org.springframework.web.client.RestTemplate
 * }</pre>
 *
 * <h3>Resulting Beans</h3>
 * For each module found in the configuration, a {@link HasFeatures} bean is registered with the name format:
 * {@code HasFeatures.{module-name}} (e.g., {@code HasFeatures.jdbc}, {@code HasFeatures.web}).
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HasFeatures
 * @see NamedFeature
 * @see AutoRegistrationBean
 * @since 1.0.0
 */
@ConditionalOnFeaturesEnabled
@ConditionalOnAvailableEndpoint(endpoint = FeaturesEndpoint.class)
@AutoConfigureBefore(name = "org.springframework.cloud.client.CommonsClientAutoConfiguration")
public class ConfigurationPropertyHasFeaturesAutoConfiguration implements BeanFactoryAware, BeanClassLoaderAware,
        EnvironmentAware {

    private static final Logger logger = getLogger(ConfigurationPropertyHasFeaturesAutoConfiguration.class);

    /**
     * The prefix of the configuration properties for {@link HasFeatures} : "microsphere.spring.cloud.features."
     */
    public static final String PROPERTY_PREFIX = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "features.";

    /**
     * The pattern of the configuration properties for abstract features: "microsphere.spring.cloud.features.{module-name}"
     */
    public static final String ABSTRACT_FEATURE_PROPERTY_NAME_PATTERN = PROPERTY_PREFIX + "{}";

    /**
     * The pattern of the configuration properties for named features: "microsphere.spring.cloud.features.{module-name}.{feature-name}"
     */
    public static final String NAMED_FEATURE_PROPERTY_NAME_PATTERN = ABSTRACT_FEATURE_PROPERTY_NAME_PATTERN + DOT + "{}";

    /**
     * The prefix of the bean name for {@link HasFeatures} : "HasFeatures."
     */
    public static final String BEAN_NAME_PREFIX = "HasFeatures.";

    private ClassLoader classLoader;

    private SingletonBeanRegistry singletonBeanRegistry;

    private final Map<String, ModuleFeatures> moduleFeaturesMap = newLinkedHashMap();

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.singletonBeanRegistry = (SingletonBeanRegistry) beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        ConfigurableEnvironment env = asConfigurableEnvironment(environment);
        Map<String, Object> subProperties = getSubProperties(env, PROPERTY_PREFIX);
        for (Entry<String, Object> entry : subProperties.entrySet()) {
            String key = entry.getKey();
            String value = valueOf(entry.getValue());
            int index = key.indexOf(DOT_CHAR);
            boolean isAbstract = index == -1;
            if (isAbstract) {
                String[] featureClassNames = split(value, COMMA_CHAR);
                String moduleName = key;
                addAbstractFeatureClassNames(moduleName, featureClassNames);
            } else {
                String moduleName = key.substring(0, index);
                String featureName = key.substring(index + 1);
                String featureClassName = value;
                addNamedFeatureClassName(moduleName, featureName, featureClassName);
            }
        }
        registerHasFeaturesBeans();
    }

    /**
     * Gets the configuration property name for abstract features of the specified module.
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // For module name "jdbc"
     * String propertyName = getAbstractFeaturePropertyName("jdbc");
     * // Result: "microsphere.spring.cloud.features.jdbc"
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
     * // Result: "microsphere.spring.cloud.features.web.rest-template"
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
     * String beanName = getBeanName("jdbc");
     * // Result: "HasFeatures.jdbc"
     * }</pre>
     *
     * @param moduleName the name of the module
     * @return the bean name for {@link HasFeatures}
     */
    public static String getBeanName(String moduleName) {
        return BEAN_NAME_PREFIX + moduleName;
    }

    /**
     * Gets the qualified feature name for a named feature of the specified module.
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // For module name "web" and feature name "rest-template"
     * String qualifiedName = getQualifierFeatureName("web", "rest-template");
     * // Result: "microsphere.web.rest-template"
     * }</pre>
     *
     * @param moduleName  the name of the module
     * @param featureName the name of the feature
     * @return the qualified feature name
     */
    public static String getQualifierFeatureName(String moduleName, String featureName) {
        return MICROSPHERE_PROPERTY_NAME_PREFIX + moduleName + DOT_CHAR + featureName;
    }

    private void addAbstractFeatureClassNames(String moduleName, String[] featureClassNames) {
        ModuleFeatures moduleFeatures = getModuleFeatures(moduleName);
        for (String featureClassName : featureClassNames) {
            Class<?> featureClass = loadClass(featureClassName);
            if (featureClass == null) {
                String propertyName = getAbstractFeaturePropertyName(moduleName);
                logger.warn("The class of abstract feature[class : '{}'] is not found in classpath, please check the configuration property : '{}'",
                        featureClassName, propertyName);
                continue;
            }
            moduleFeatures.abstractFeatures.add(featureClass);
        }
    }

    private void addNamedFeatureClassName(String moduleName, String featureName, String featureClassName) {
        ModuleFeatures moduleFeatures = getModuleFeatures(moduleName);
        String name = getQualifierFeatureName(moduleName, featureName);
        Class<?> featureClass = loadClass(featureClassName);
        if (featureClass == null) {
            String propertyName = getNamedFeaturePropertyName(moduleName, featureName);
            logger.warn("The class of named feature[name : '{}' , class : '{}'] is not found in classpath, please check the configuration property : '{}'",
                    name, featureClassName, propertyName);
            return;
        }
        NamedFeature namedFeature = new NamedFeature(name, featureClass);
        moduleFeatures.namedFeatures.add(namedFeature);
    }

    private Class<?> loadClass(String className) {
        return resolveClass(className, this.classLoader);
    }

    private ModuleFeatures getModuleFeatures(String moduleName) {
        return this.moduleFeaturesMap.computeIfAbsent(moduleName, ModuleFeatures::new);
    }

    private void registerHasFeaturesBeans() {
        for (Entry<String, ModuleFeatures> entry : this.moduleFeaturesMap.entrySet()) {
            String moduleName = entry.getKey();
            ModuleFeatures moduleFeatures = entry.getValue();
            HasFeatures hasFeatures = moduleFeatures.toHasFeatures();
            String beanName = getBeanName(moduleName);
            this.singletonBeanRegistry.registerSingleton(beanName, hasFeatures);
        }
    }

    static class ModuleFeatures {

        private final String moduleName;

        private final List<Class<?>> abstractFeatures = newLinkedList();

        private final List<NamedFeature> namedFeatures = newLinkedList();

        ModuleFeatures(String moduleName) {
            this.moduleName = moduleName;
        }

        HasFeatures toHasFeatures() {
            logger.info(toString());
            return new HasFeatures(this.abstractFeatures, this.namedFeatures);
        }

        @Override
        public String toString() {
            return format("The module[name : '{}'] has abstract features : {} and named features : {}",
                    this.moduleName, this.abstractFeatures, this.namedFeatures);
        }
    }
}
