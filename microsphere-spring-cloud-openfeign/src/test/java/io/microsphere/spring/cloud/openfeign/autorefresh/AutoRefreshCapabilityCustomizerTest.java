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


import io.microsphere.spring.test.junit.jupiter.SpringLoggingTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.context.named.NamedContextFactory.Specification;

import java.lang.reflect.Constructor;

import static io.microsphere.reflect.ConstructorUtils.findConstructor;
import static io.microsphere.reflect.ConstructorUtils.newInstance;
import static io.microsphere.spring.cloud.openfeign.autorefresh.AutoRefreshCapabilityCustomizer.AUTO_REFRESH_CAPABILITY_CLASS;
import static io.microsphere.spring.cloud.openfeign.autorefresh.AutoRefreshCapabilityCustomizer.FEIGN_CLIENT_SPECIFICATION_CLASS;
import static io.microsphere.util.ArrayUtils.combine;
import static io.microsphere.util.ArrayUtils.ofArray;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * {@link AutoRefreshCapabilityCustomizer} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AutoRefreshCapabilityCustomizer
 * @since 1.0.0
 */
@SpringLoggingTest
class AutoRefreshCapabilityCustomizerTest {

    private AutoRefreshCapabilityCustomizer autoRefreshCapabilityCustomizer;

    @BeforeEach
    void setUp() {
        this.autoRefreshCapabilityCustomizer = new AutoRefreshCapabilityCustomizer();
    }

    @Test
    void testInjectAutoRefreshCapability() {
        Class<?>[] configurationClasses = ofArray(AutoRefreshCapabilityCustomizerTest.class);
        Specification specification = newSpecification(configurationClasses);
        assertArrayEquals(configurationClasses, specification.getConfiguration());
        this.autoRefreshCapabilityCustomizer.injectAutoRefreshCapability(specification);
        assertArrayEquals(combine(AUTO_REFRESH_CAPABILITY_CLASS, configurationClasses), specification.getConfiguration());
    }

    private Specification newSpecification(Class<?>[] configurationClasses) {
        Constructor<?> constructor = findConstructor(FEIGN_CLIENT_SPECIFICATION_CLASS, String.class, Class[].class);
        return (Specification) newInstance(constructor, "test", configurationClasses);
    }
}