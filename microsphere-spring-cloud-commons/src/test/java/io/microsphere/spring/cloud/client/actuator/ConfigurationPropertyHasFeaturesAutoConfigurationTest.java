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


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.cloud.client.actuator.NamedFeature;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static io.microsphere.spring.cloud.client.actuator.ConfigurationPropertyHasFeaturesAutoConfiguration.getBeanName;
import static io.microsphere.spring.cloud.client.actuator.ConfigurationPropertyHasFeaturesAutoConfiguration.getQualifierFeatureName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ConfigurationPropertyHasFeaturesAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConfigurationPropertyHasFeaturesAutoConfiguration
 * @since 1.0.0
 */
@SpringBootTest(
        classes = ConfigurationPropertyHasFeaturesAutoConfigurationTest.class,
        properties = {
                "microsphere.spring.cloud.features.jdbc.JdbcTemplate=org.springframework.jdbc.core.JdbcTemplate",
                "microsphere.spring.cloud.features.rest.RestTemplate=org.springframework.web.client.RestTemplate",
                "microsphere.spring.cloud.features.rest=org.springframework.web.client.RestOperations",
                "microsphere.spring.cloud.features.redis.RedisConnection=org.springframework.data.redis.connection.RedisConnection",
                "microsphere.spring.cloud.features.redis=org.springframework.data.redis.connection.RedisConnectionFactory," +
                        "org.springframework.data.redis.core.RedisTemplate," +
                        "org.springframework.data.redis.core.StringRedisTemplate",
        }
)
@EnableAutoConfiguration
class ConfigurationPropertyHasFeaturesAutoConfigurationTest {

    @Autowired
    private Map<String, HasFeatures> hasFeaturesBeansMap;

    @Test
    void test() {
        HasFeatures hasFeatures = this.hasFeaturesBeansMap.get(getBeanName("jdbc"));
        assertNotNull(hasFeatures);
        assertTrue(hasFeatures.getAbstractFeatures().isEmpty());
        assertTrue(hasFeatures.getNamedFeatures().isEmpty());

        hasFeatures = this.hasFeaturesBeansMap.get(getBeanName("rest"));
        assertNotNull(hasFeatures);

        List<Class<?>> abstractFeatures = hasFeatures.getAbstractFeatures();
        assertEquals(1, abstractFeatures.size());
        assertEquals(RestOperations.class, abstractFeatures.get(0));

        List<NamedFeature> namedFeatures = hasFeatures.getNamedFeatures();
        assertEquals(1, namedFeatures.size());
        NamedFeature namedFeature = namedFeatures.get(0);
        assertEquals(getQualifierFeatureName("rest", "RestTemplate"), namedFeature.getName());
        assertEquals(RestTemplate.class, namedFeature.getType());

        hasFeatures = this.hasFeaturesBeansMap.get(getBeanName("redis"));
        assertNotNull(hasFeatures);
        assertTrue(hasFeatures.getAbstractFeatures().isEmpty());
        assertTrue(hasFeatures.getNamedFeatures().isEmpty());

    }

}