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


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.actuator.FeaturesEndpoint;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.cloud.client.actuator.NamedFeature;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static io.microsphere.spring.cloud.client.actuator.FeaturesUtils.getHasFeaturesBeanName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ConfigurationPropertyHasFeaturesAutoConfiguration} Integration Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurationPropertyHasFeaturesAutoConfiguration
 * @since 1.0.0
 */
@SpringBootTest(
        classes = {
                RestTemplate.class,
                ConfigurationPropertyHasFeaturesAutoConfigurationIntegrationTest.class
        },
        properties = {
                "microsphere.spring.cloud.features.abstract.rest=org.springframework.web.client.RestOperations",
                "microsphere.spring.cloud.features.abstract.redis=org.springframework.data.redis.connection.RedisConnectionFactory," +
                        "org.springframework.data.redis.core.RedisTemplate," +
                        "org.springframework.data.redis.core.StringRedisTemplate",
                "microsphere.spring.cloud.features.named.jdbc.JdbcTemplate=org.springframework.jdbc.core.JdbcTemplate",
                "microsphere.spring.cloud.features.named.rest.RestTemplate=org.springframework.web.client.RestTemplate",
                "microsphere.spring.cloud.features.named.redis.RedisConnection=org.springframework.data.redis.connection.RedisConnection",
                "microsphere.spring.cloud.features.named.web.WebClient=org.springframework.web.reactive.function.client.WebClient"
        }
)
@EnableAutoConfiguration
class ConfigurationPropertyHasFeaturesAutoConfigurationIntegrationTest {

    @Autowired
    private Map<String, HasFeatures> hasFeaturesBeansMap;

    @Autowired
    private FeaturesEndpoint featuresEndpoint;

    @Test
    void test() {
        HasFeatures hasFeatures = this.hasFeaturesBeansMap.get(getHasFeaturesBeanName("jdbc"));
        assertNull(hasFeatures);

        hasFeatures = this.hasFeaturesBeansMap.get(getHasFeaturesBeanName("redis"));
        assertNull(hasFeatures);

        hasFeatures = this.hasFeaturesBeansMap.get(getHasFeaturesBeanName("web"));
        assertNotNull(hasFeatures);
        List<Class<?>> abstractFeatures = hasFeatures.getAbstractFeatures();
        List<NamedFeature> namedFeatures = hasFeatures.getNamedFeatures();
        assertEquals(1, namedFeatures.size());
        assertEquals("web:WebClient", namedFeatures.get(0).getName());
        assertTrue(abstractFeatures.isEmpty());

        hasFeatures = this.hasFeaturesBeansMap.get(getHasFeaturesBeanName("rest"));
        assertNotNull(hasFeatures);

        abstractFeatures = hasFeatures.getAbstractFeatures();
        namedFeatures = hasFeatures.getNamedFeatures();
        assertEquals("rest:RestTemplate", namedFeatures.get(0).getName());
        assertEquals(1, namedFeatures.size());
        assertTrue(abstractFeatures.isEmpty());


        assertNotNull(featuresEndpoint.features());
    }
}