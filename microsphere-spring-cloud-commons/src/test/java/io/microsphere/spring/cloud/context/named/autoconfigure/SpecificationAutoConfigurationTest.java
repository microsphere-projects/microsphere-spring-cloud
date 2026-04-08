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

package io.microsphere.spring.cloud.context.named.autoconfigure;


import io.microsphere.spring.cloud.context.named.SpecificationCustomizer;
import io.microsphere.spring.cloud.context.named.config.SpecificationBeanPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.named.NamedContextFactory.Specification;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link SpecificationAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SpecificationAutoConfiguration
 * @since 1.0.0
 */
@SpringBootTest(
        classes = SpecificationAutoConfigurationTest.Config.class
)
@EnableAutoConfiguration
class SpecificationAutoConfigurationTest implements Specification {

    @Autowired
    private SpecificationBeanPostProcessor specificationBeanPostProcessor;

    @Autowired
    private SpecificationCustomizer specificationCustomizer;

    @Test
    void testBeans() {
        assertNotNull(specificationBeanPostProcessor);
        assertNotNull(specificationCustomizer);
    }

    static class Config {

        @Bean
        public static SpecificationCustomizer customizer() {
            return (spec, name) -> {
                assertNotNull(spec);
                assertNotNull(name);
            };
        }
    }


    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Class<?>[] getConfiguration() {
        return new Class[0];
    }
}