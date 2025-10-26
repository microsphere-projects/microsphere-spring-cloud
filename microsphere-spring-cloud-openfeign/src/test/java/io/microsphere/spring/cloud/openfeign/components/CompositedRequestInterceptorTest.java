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


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor.INSTANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link CompositedRequestInterceptor} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CompositedRequestInterceptor
 * @since 1.0.0
 */
class CompositedRequestInterceptorTest {

    private static final String TEST_NAME = "test-name";

    private static final String TEST_VALUE = "test-value";

    protected String contextId;

    private FeignClientProperties feignClientProperties;

    private GenericApplicationContext context;

    private CompositedRequestInterceptor interceptor;

    @BeforeEach
    void setUp() {
        this.contextId = "test-context";
        this.feignClientProperties = new FeignClientProperties();
        this.context = new GenericApplicationContext();
        this.context.setId(contextId);
        this.context.registerBean(FeignClientProperties.class, () -> feignClientProperties);
        this.context.refresh();
        this.interceptor = new CompositedRequestInterceptor(this.contextId, this.context);
        initFeignClientProperties();
    }

    void initFeignClientProperties() {
        Map<String, FeignClientConfiguration> config = this.feignClientProperties.getConfig();
        config.put(this.contextId, new FeignClientConfiguration());
        config.put(this.feignClientProperties.getDefaultConfig(), new FeignClientConfiguration());
    }

    FeignClientConfiguration getDefaultConfiguration() {
        return this.feignClientProperties.getConfig().get(this.feignClientProperties.getDefaultConfig());
    }

    FeignClientConfiguration getCurrentConfiguration() {
        return this.feignClientProperties.getConfig().get(this.contextId);
    }

    @Test
    void testGetRequestInterceptors() {
        assertTrue(this.interceptor.getRequestInterceptors().isEmpty());
    }

    @Test
    void testAddRequestInterceptor() {
        this.interceptor.addRequestInterceptor(INSTANCE);
        assertEquals(1, this.interceptor.getRequestInterceptors().size());
        assertTrue(this.interceptor.getRequestInterceptors().contains(INSTANCE));
    }

    @Test
    void testApply() {
        RequestTemplate template = new RequestTemplate();
        this.interceptor.apply(template);
        this.interceptor.addRequestInterceptor(INSTANCE);
        this.interceptor.apply(template);
    }

    @Test
    void testRefresh() {
        this.interceptor.refresh();
    }

    @Test
    void testRefreshOnDefaultConfiguration() {
        FeignClientConfiguration defaultConfiguration = getDefaultConfiguration();
        testRefresh(defaultConfiguration);
    }

    @Test
    void testRefreshOnCurrentConfiguration() {
        FeignClientConfiguration currentConfiguration = getCurrentConfiguration();
        addRequestInterceptor(currentConfiguration, TestRequestInterceptor.class);
        testRefresh(currentConfiguration);
    }

    void testRefresh(FeignClientConfiguration configuration) {
        initFeignClientConfiguration(configuration);
        this.interceptor.refresh();

        RequestTemplate template = new RequestTemplate();
        this.interceptor.apply(template);
    }

    void initFeignClientConfiguration(FeignClientConfiguration configuration) {
        addRequestInterceptor(configuration, NoOpRequestInterceptor.class);

        Map<String, Collection<String>> headers = configuration.getDefaultRequestHeaders();
        if (headers == null) {
            headers = new HashMap<>();
            configuration.setDefaultRequestHeaders(headers);
        }
        add(headers, TEST_NAME, TEST_VALUE);

        Map<String, Collection<String>> parameters = configuration.getDefaultQueryParameters();
        if (parameters == null) {
            parameters = new HashMap<>();
            configuration.setDefaultQueryParameters(parameters);
        }
        add(parameters, TEST_NAME, TEST_VALUE);
    }

    void addRequestInterceptor(FeignClientConfiguration configuration, Class<? extends RequestInterceptor> requestInterceptorClass) {
        List<Class<RequestInterceptor>> requestInterceptors = configuration.getRequestInterceptors();
        if (requestInterceptors == null) {
            requestInterceptors = new ArrayList<>();
            configuration.setRequestInterceptors(requestInterceptors);
        }
        requestInterceptors.add((Class<RequestInterceptor>) requestInterceptorClass);
    }

    void add(Map<String, Collection<String>> map, String name, String value) {
        Collection<String> values = map.computeIfAbsent(name, n -> new ArrayList<>());
        values.add(value);
    }

    static class TestRequestInterceptor implements RequestInterceptor {

        @Override
        public void apply(RequestTemplate template) {
            template.query(TEST_NAME, TEST_VALUE);
            template.header(TEST_NAME, TEST_VALUE);
        }
    }
}