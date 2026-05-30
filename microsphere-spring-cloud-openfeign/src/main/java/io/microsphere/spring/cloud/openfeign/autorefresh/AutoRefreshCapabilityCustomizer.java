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

package io.microsphere.spring.cloud.openfeign.autorefresh;

import io.microsphere.beans.BeanMetadata;
import io.microsphere.logging.Logger;
import io.microsphere.spring.cloud.context.named.SpecificationCustomizer;
import org.springframework.cloud.context.named.NamedContextFactory.Specification;

import java.lang.reflect.Method;

import static io.microsphere.beans.BeanUtils.findWriteMethod;
import static io.microsphere.beans.BeanUtils.getBeanMetadata;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.reflect.MethodUtils.invokeMethod;
import static io.microsphere.util.ArrayUtils.arrayToString;
import static io.microsphere.util.ArrayUtils.combine;
import static io.microsphere.util.ClassLoaderUtils.resolveClass;

/**
 * The {@link SpecificationCustomizer} class to register the bean of {@link AutoRefreshCapability}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AutoRefreshCapability
 * @since 1.0.0
 */
class AutoRefreshCapabilityCustomizer implements SpecificationCustomizer {

    private static final Logger logger = getLogger(AutoRefreshCapabilityCustomizer.class);

    static final String FEIGN_CLIENT_SPECIFICATION_CLASS_NAME = "org.springframework.cloud.openfeign.FeignClientSpecification";

    static final Class<?> FEIGN_CLIENT_SPECIFICATION_CLASS = resolveClass(FEIGN_CLIENT_SPECIFICATION_CLASS_NAME);

    /**
     * The class of {@link AutoRefreshCapability}
     */
    public static final Class<?> AUTO_REFRESH_CAPABILITY_CLASS = AutoRefreshCapability.class;

    @Override
    public void customize(Specification specification, String beanName) {
        if (isFeignClientSpecification(specification) && beanName.startsWith("default.")) {
            injectAutoRefreshCapability(specification);
        }
    }

    boolean isFeignClientSpecification(Specification specification) {
        return FEIGN_CLIENT_SPECIFICATION_CLASS.equals(specification.getClass());
    }

    void injectAutoRefreshCapability(Specification specification) {
        Class<?>[] originConfigurationClasses = specification.getConfiguration();
        Class<?>[] newConfigurationClasses = combine(AUTO_REFRESH_CAPABILITY_CLASS, originConfigurationClasses);

        BeanMetadata beanMetadata = getBeanMetadata(FEIGN_CLIENT_SPECIFICATION_CLASS);
        Method setConfigurationMethod = findWriteMethod(beanMetadata, "configuration");
        invokeMethod(specification, setConfigurationMethod, new Object[]{newConfigurationClasses});

        if (logger.isTraceEnabled()) {
            logger.trace("The Configuration classes: before - {} , after - {}", arrayToString(originConfigurationClasses),
                    arrayToString(newConfigurationClasses));
        }
    }
}