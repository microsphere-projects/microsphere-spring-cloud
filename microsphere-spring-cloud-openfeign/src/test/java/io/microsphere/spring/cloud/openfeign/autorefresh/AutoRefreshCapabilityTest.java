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


import feign.Contract;
import feign.QueryMapEncoder;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClientFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link AutoRefreshCapability} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AutoRefreshCapability
 * @since 1.0.0
 */
class AutoRefreshCapabilityTest {

    private String contextId;

    private FeignClientProperties feignClientProperties;

    private FeignClientFactory feignClientFactory;

    private GenericApplicationContext context;

    private FeignComponentRegistry feignComponentRegistry;

    private AutoRefreshCapability capability;

    @BeforeEach
    void setUp() {
        this.contextId = "test-context";
        this.feignClientProperties = new FeignClientProperties();
        this.feignClientFactory = new FeignClientFactory();
        this.context = new GenericApplicationContext();
        this.context.setId(contextId);
        this.context.registerBean(FeignClientProperties.class, () -> this.feignClientProperties);
        this.context.refresh();
        this.feignComponentRegistry = new FeignComponentRegistry(this.contextId, this.context);
        this.capability = new AutoRefreshCapability(this.feignClientProperties, this.feignClientFactory, this.feignComponentRegistry);
        initFeignClientProperties();
    }

    void initFeignClientProperties() {
        Map<String, FeignClientProperties.FeignClientConfiguration> config = this.feignClientProperties.getConfig();
        config.put(this.contextId, new FeignClientProperties.FeignClientConfiguration());
        config.put(this.feignClientProperties.getDefaultConfig(), new FeignClientProperties.FeignClientConfiguration());
    }

    @Test
    void testEnrich() {
        assertNull(this.capability.enrich((Retryer) null));
        assertNull(this.capability.enrich((Contract) null));
        assertNull(this.capability.enrich((Decoder) null));
        assertNull(this.capability.enrich((Encoder) null));
        assertNull(this.capability.enrich((ErrorDecoder) null));
        assertNull(this.capability.enrich((RequestInterceptor) null));
        assertNull(this.capability.enrich((QueryMapEncoder) null));
    }
}