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
package io.microsphere.spring.cloud.client.condition;

/**
 * {@link ConditionalOnFeaturesEnabled} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ConditionalOnFeaturesEnabled
 * @since 1.0.0
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ConditionalOnFeaturesEnabledTest.FeaturesConfiguration.class
})
@TestPropertySource(
        properties = {
                "spring.cloud.features.enabled=true"
        }
)
public class ConditionalOnFeaturesEnabledTest {

    @ConditionalOnFeaturesEnabled
    static class FeaturesConfiguration {
    }

    @Autowired
    private ObjectProvider<FeaturesConfiguration> featuresConfigurationProvider;

    @Test
    public void test() {
        assertNotNull(featuresConfigurationProvider.getIfAvailable());
    }
}
