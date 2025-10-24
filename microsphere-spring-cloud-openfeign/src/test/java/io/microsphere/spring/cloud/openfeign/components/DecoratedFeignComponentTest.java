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

package io.microsphere.spring.cloud.openfeign.components;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;

import java.util.Map;

/**
 * Abstract {@link DecoratedFeignComponent} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedFeignComponent
 * @since 1.0.0
 */
abstract class DecoratedFeignComponentTest {

    protected String contextId;

    protected NamedContextFactory<FeignClientSpecification> contextFactory;

    protected FeignClientProperties clientProperties;

    @BeforeEach
    void setUp() {
        this.contextId = "test-context";
        this.contextFactory = new FeignClientFactory();
        this.clientProperties = new FeignClientProperties();
    }

    void initDefaultConfiguration() {
        String defaultConfig = this.clientProperties.getDefaultConfig();
        setConfiguration(defaultConfig, new FeignClientConfiguration());
    }

    void initCurrentConfiguration() {
        setConfiguration(this.contextId, new FeignClientConfiguration());
    }

    void setConfiguration(String id, FeignClientConfiguration configuration) {
        Map<String, FeignClientConfiguration> config = this.clientProperties.getConfig();
        config.put(id, configuration);
    }
}