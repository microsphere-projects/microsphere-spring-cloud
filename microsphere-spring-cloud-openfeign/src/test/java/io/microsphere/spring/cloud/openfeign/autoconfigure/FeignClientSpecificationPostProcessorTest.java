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

package io.microsphere.spring.cloud.openfeign.autoconfigure;


import io.microsphere.spring.test.junit.jupiter.SpringLoggingTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClientSpecification;

import static io.microsphere.spring.cloud.openfeign.autoconfigure.FeignClientSpecificationPostProcessor.AUTO_REFRESH_CAPABILITY_CLASS;
import static io.microsphere.util.ArrayUtils.combine;
import static io.microsphere.util.ArrayUtils.ofArray;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * {@link FeignClientSpecificationPostProcessor} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see FeignClientSpecificationPostProcessor
 * @since 1.0.0
 */
@SpringLoggingTest
class FeignClientSpecificationPostProcessorTest {

    private FeignClientSpecificationPostProcessor postProcessor;

    @BeforeEach
    void setUp() {
        this.postProcessor = new FeignClientSpecificationPostProcessor();
    }

    @Test
    void testInjectAutoRefreshCapability() {
        Class<?>[] configurationClasses = ofArray(FeignClientSpecificationPostProcessorTest.class);
        FeignClientSpecification specification = new FeignClientSpecification("test", "TestClass", configurationClasses);
        assertArrayEquals(configurationClasses, specification.getConfiguration());
        this.postProcessor.injectAutoRefreshCapability(specification);
        assertArrayEquals(combine(AUTO_REFRESH_CAPABILITY_CLASS, configurationClasses), specification.getConfiguration());
    }
}