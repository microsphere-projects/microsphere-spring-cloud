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

import io.microsphere.logging.Logger;
import io.microsphere.spring.cloud.context.named.SpecificationCustomizer;
import org.springframework.cloud.context.named.NamedContextFactory.Specification;
import org.springframework.cloud.openfeign.FeignClientSpecification;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.util.ArrayUtils.arrayToString;
import static io.microsphere.util.ArrayUtils.combine;

/**
 * The {@link SpecificationCustomizer} class to register the bean of {@link AutoRefreshCapability}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AutoRefreshCapability
 * @since 1.0.0
 */
class AutoRefreshCapabilityCustomizer implements SpecificationCustomizer {

    private static final Logger logger = getLogger(AutoRefreshCapabilityCustomizer.class);

    /**
     * The class of {@link AutoRefreshCapability}
     */
    public static final Class<?> AUTO_REFRESH_CAPABILITY_CLASS = AutoRefreshCapability.class;

    @Override
    public void customize(Specification specification, String beanName) {
        if (specification instanceof FeignClientSpecification && beanName.startsWith("default.")) {
            injectAutoRefreshCapability((FeignClientSpecification) specification);
        }
    }

    void injectAutoRefreshCapability(FeignClientSpecification specification) {
        Class<?>[] originConfigurationClasses = specification.getConfiguration();
        Class<?>[] newConfigurationClasses = combine(AUTO_REFRESH_CAPABILITY_CLASS, originConfigurationClasses);
        specification.setConfiguration(newConfigurationClasses);
        if (logger.isTraceEnabled()) {
            logger.trace("The Configuration classes: before - {} , after - {}", arrayToString(originConfigurationClasses),
                    arrayToString(newConfigurationClasses));
        }
    }
}