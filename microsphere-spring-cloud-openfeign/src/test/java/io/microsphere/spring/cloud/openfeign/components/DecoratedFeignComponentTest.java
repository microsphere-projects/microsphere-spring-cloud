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

import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClientSpecification;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;

import java.util.Map;

import static feign.Request.HttpMethod.GET;
import static feign.Request.create;
import static feign.Response.builder;
import static io.microsphere.collection.MapUtils.newHashMap;
import static io.microsphere.spring.cloud.openfeign.components.DecoratedFeignComponent.instantiate;
import static io.microsphere.util.ArrayUtils.EMPTY_BYTE_ARRAY;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.core.ResolvableType.forClass;

/**
 * Abstract {@link DecoratedFeignComponent} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedFeignComponent
 * @since 1.0.0
 */
abstract class DecoratedFeignComponentTest<T, D extends DecoratedFeignComponent<T>> {

    protected String contextId;

    protected NamedContextFactory<FeignClientSpecification> contextFactory;

    protected FeignClientProperties clientProperties;

    protected Class<T> componentClass;

    protected Class<D> decoratedComponentClass;

    protected T delegate;

    protected D decoratedComponent;

    @BeforeEach
    void setUp() {
        this.contextId = "test-context";
        this.contextFactory = new FeignClientFactory(applicationContextInitializers());
        this.clientProperties = new FeignClientProperties();

        ResolvableType resolvableType = forClass(this.getClass()).as(DecoratedFeignComponentTest.class);
        this.componentClass = (Class<T>) resolvableType.getGeneric(0).resolve();
        this.decoratedComponentClass = (Class<D>) resolvableType.getGeneric(1).resolve();

        this.delegate = createDelegate();
        this.decoratedComponent = instantiate(decoratedComponentClass, componentClass, contextId, contextFactory, clientProperties, delegate);
    }

    @Test
    void testComponentTypeFromDefaultConfiguration() {
        initDefaultConfiguration();
        Class<T> delegateClass = getDelegateClass();
        configureDelegateClass(this.decoratedComponent.getDefaultConfiguration(), delegateClass);
        assertSame(delegateClass, this.decoratedComponent.componentType());
    }

    @Test
    void testComponentTypeFromCurrentConfiguration() {
        initCurrentConfiguration();
        Class<T> delegateClass = getDelegateClass();
        configureDelegateClass(this.decoratedComponent.getCurrentConfiguration(), delegateClass);
        assertSame(delegateClass, this.decoratedComponent.componentType());
    }

    @Test
    void testComponentType() {
        assertSame(componentClass, this.decoratedComponent.componentType());
    }

    @Test
    void testEquals() {
        assertEquals(this.decoratedComponent, this.delegate);
    }

    @Test
    void testHashCode() {
        assertEquals(this.decoratedComponent.hashCode(), this.delegate.hashCode());
    }

    protected abstract T createDelegate();

    protected abstract void configureDelegateClass(FeignClientConfiguration configuration, Class<T> delegateClass);

    protected Class<T> getDelegateClass() {
        return (Class<T>) this.delegate.getClass();
    }

    protected Map<String, ApplicationContextInitializer<GenericApplicationContext>> applicationContextInitializers() {
        return newHashMap();
    }

    protected Request createTestRequest() {
        return create(GET, "http://localhost", emptyMap(), EMPTY_BYTE_ARRAY, UTF_8, new RequestTemplate());
    }

    protected Response createTestResponse() {
        return builder()
                .status(200)
                .request(createTestRequest())
                .body(new byte[1024])
                .build();
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