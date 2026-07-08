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

package io.microsphere.spring.cloud.client.actuator.autoconfigure;

import io.microsphere.logging.Logger;
import io.microsphere.spring.cloud.client.actuator.FeaturesProperties;
import io.microsphere.spring.cloud.client.actuator.constants.FeaturesConstants;
import io.microsphere.spring.cloud.client.condition.ConditionalOnFeaturesAvailable;
import io.microsphere.spring.context.config.AutoRegistrationBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.cloud.client.actuator.NamedFeature;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static io.microsphere.collection.ListUtils.ofList;
import static io.microsphere.collection.MapUtils.newLinkedHashMap;
import static io.microsphere.collection.SetUtils.newLinkedHashSet;
import static io.microsphere.collection.SetUtils.newTreeSet;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.spring.beans.BeanSource.BEAN_FACTORY;
import static io.microsphere.spring.beans.factory.BeanFactoryUtils.asDefaultListableBeanFactory;
import static io.microsphere.spring.cloud.client.actuator.FeaturesUtils.getAbstractFeaturePropertyName;
import static io.microsphere.spring.cloud.client.actuator.FeaturesUtils.getHasFeaturesBeanName;
import static io.microsphere.spring.cloud.client.actuator.FeaturesUtils.getNamedFeaturePropertyName;
import static io.microsphere.spring.cloud.client.actuator.FeaturesUtils.getQualifierFeatureName;
import static io.microsphere.spring.cloud.client.actuator.NamedFeatureComparator.INSTANCE;
import static io.microsphere.text.FormatUtils.format;
import static io.microsphere.util.ClassLoaderUtils.resolveClass;
import static io.microsphere.util.ClassUtils.getSimpleName;

/**
 * Auto-registrar for Spring Cloud Client Actuator's {@link HasFeatures} based on configuration properties.
 * <p>
 * This class scans configuration properties under the prefix {@value FeaturesConstants#PROPERTY_NAME_PREFIX} to
 * automatically register {@link HasFeatures} beans. It supports two types of feature definitions:
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
 * microsphere.spring.cloud.features.abstract.jdbc=org.springframework.jdbc.core.JdbcTemplate,org.springframework.transaction.PlatformTransactionManager
 * }</pre>
 *
 * <h4>2. Named Features</h4>
 * <pre>{@code
 * # Defines a named feature 'rest-template' for the 'web' module
 * microsphere.spring.cloud.features.named.web.rest-template=org.springframework.web.client.RestTemplate
 * }</pre>
 *
 * <h3>Resulting Beans</h3>
 * For each module found in the configuration, a {@link HasFeatures} bean is registered with the name format:
 * {@code HasFeatures.{module-name}} (e.g., {@code jdbc.features}, {@code web.features}).
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see HasFeatures
 * @see NamedFeature
 * @see AutoRegistrationBean
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnFeaturesAvailable
@AutoConfigureBefore(name = {
        "org.springframework.cloud.client.CommonsClientAutoConfiguration"             // Spring Cloud Commons API
})
@EnableConfigurationProperties(FeaturesProperties.class)
public class ConfigurationPropertyHasFeaturesAutoConfiguration implements BeanFactoryAware, BeanClassLoaderAware, InitializingBean {

    private static final Logger logger = getLogger(ConfigurationPropertyHasFeaturesAutoConfiguration.class);

    private ClassLoader classLoader;

    private DefaultListableBeanFactory beanFactory;

    @Autowired
    private FeaturesProperties featuresProperties;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = asDefaultListableBeanFactory(beanFactory);
    }

    @Override
    public void afterPropertiesSet() {
        FeaturesProperties featuresProperties = this.featuresProperties;

        Map<String, ModuleFeatures> moduleFeaturesMap = newLinkedHashMap();
        Map<String, List<String>> abstractProperties = featuresProperties.getAbstract();

        abstractProperties.forEach((moduleName, featureClassNames) -> {
            addAbstractFeatureClassNames(moduleName, featureClassNames, moduleFeaturesMap);
        });

        Map<String, Map<String, String>> namedProperties = featuresProperties.getNamed();

        namedProperties.forEach((moduleName, namedFeatures) -> {
            for (Entry<String, String> entry : namedFeatures.entrySet()) {
                String featureName = entry.getKey();
                String featureClassName = entry.getValue();
                addNamedFeatureClassName(moduleName, featureName, featureClassName, moduleFeaturesMap);
            }
        });

        registerHasFeaturesBeans(moduleFeaturesMap);
        moduleFeaturesMap.clear();
    }

    private void addAbstractFeatureClassNames(String moduleName, List<String> featureClassNames, Map<String, ModuleFeatures> moduleFeaturesMap) {
        for (String featureClassName : featureClassNames) {
            addAbstractFeatureClassName(moduleName, featureClassName, moduleFeaturesMap);
        }
    }

    private void addAbstractFeatureClassName(String moduleName, String featureClassName, Map<String, ModuleFeatures> moduleFeaturesMap) {
        Class<?> featureClass = loadClass(featureClassName);
        if (featureClass == null) {
            logger.warn("The class of AbstractFeature[class : '{}'] is not found in classpath, please check the configuration property : '{}'",
                    featureClassName, getAbstractFeaturePropertyName(moduleName));
            return;
        }

        Set<Class<?>> beanTypes = getBeanTypes(featureClass);
        if (beanTypes.isEmpty()) { // No bean type found
            addAbstractFeatureClass(moduleName, featureClass, moduleFeaturesMap);
        } else {
            for (Class<?> beanType : beanTypes) {
                addNamedFeatureClass(moduleName, beanType, moduleFeaturesMap);
            }
        }
    }

    private void addNamedFeatureClassName(String moduleName, String featureName, String featureClassName, Map<String, ModuleFeatures> moduleFeaturesMap) {
        String name = getQualifierFeatureName(moduleName, featureName);
        Class<?> featureClass = loadClass(featureClassName);
        String propertyName = getNamedFeaturePropertyName(moduleName, featureName);
        if (featureClass == null) {
            logger.warn("The class of named feature[name : '{}' , class : '{}'] is not found in classpath, please check the configuration property : '{}'",
                    name, featureClassName, propertyName);
            return;
        }

        addNamedFeatureClass(moduleName, featureName, featureClass, moduleFeaturesMap);
    }

    private Set<Class<?>> getBeanTypes(Class<?> featureClass) {
        return (Set) BEAN_FACTORY.getBeanTypes(this.beanFactory, featureClass);
    }

    private void addAbstractFeatureClass(String moduleName, Class<?> featureClass, Map<String, ModuleFeatures> moduleFeaturesMap) {
        ModuleFeatures moduleFeatures = getModuleFeatures(moduleFeaturesMap, moduleName);
        logger.trace("The AbstractFeature[module : '{}' , class : '{}'] will be added in the HasFeatures.", moduleName,
                featureClass.getName());
        moduleFeatures.abstractFeatures.add(featureClass);
    }

    private void addNamedFeatureClass(String moduleName, Class<?> featureClass, Map<String, ModuleFeatures> moduleFeaturesMap) {
        String featureName = getSimpleName(featureClass);
        addNamedFeatureClass(moduleName, featureName, featureClass, moduleFeaturesMap);
    }

    private void addNamedFeatureClass(String moduleName, String featureName, Class<?> featureClass, Map<String, ModuleFeatures> moduleFeaturesMap) {
        String name = getQualifierFeatureName(moduleName, featureName);
        NamedFeature namedFeature = new NamedFeature(name, featureClass);
        ModuleFeatures moduleFeatures = getModuleFeatures(moduleFeaturesMap, moduleName);
        logger.trace("The NamedFeature[module : '{}' , name : '{}' , class : '{}'] will be added in the HasFeatures.",
                moduleName, name, featureClass.getName());
        moduleFeatures.namedFeatures.add(namedFeature);
    }


    private Class<?> loadClass(String className) {
        return resolveClass(className, this.classLoader);
    }

    private ModuleFeatures getModuleFeatures(Map<String, ModuleFeatures> moduleFeaturesMap, String moduleName) {
        return moduleFeaturesMap.computeIfAbsent(moduleName, ModuleFeatures::new);
    }

    private void registerHasFeaturesBeans(Map<String, ModuleFeatures> moduleFeaturesMap) {
        for (Entry<String, ModuleFeatures> entry : moduleFeaturesMap.entrySet()) {
            String moduleName = entry.getKey();
            ModuleFeatures moduleFeatures = entry.getValue();
            HasFeatures hasFeatures = moduleFeatures.toHasFeatures();
            String beanName = getHasFeaturesBeanName(moduleName);
            this.beanFactory.registerSingleton(beanName, hasFeatures);
        }
    }

    static class ModuleFeatures {

        private final String moduleName;

        private final Set<Class<?>> abstractFeatures = newLinkedHashSet();

        private final Set<NamedFeature> namedFeatures = newTreeSet(INSTANCE);

        ModuleFeatures(String moduleName) {
            this.moduleName = moduleName;
        }

        HasFeatures toHasFeatures() {
            logger.info(toString());
            return new HasFeatures(ofList(this.abstractFeatures), ofList(this.namedFeatures));
        }

        @Override
        public String toString() {
            return format("The module[name : '{}'] has abstract features : {} and named features : {}",
                    this.moduleName, this.abstractFeatures, this.namedFeatures);
        }
    }
}
