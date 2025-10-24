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
import feign.RetryableException;
import feign.Retryer;
import feign.Retryer.Default;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Date;
import java.util.Map;

import static feign.Request.create;
import static io.microsphere.spring.cloud.openfeign.components.DecoratedFeignComponent.instantiate;
import static io.microsphere.spring.cloud.openfeign.components.DecoratedRetryer.continueOrPropagate;
import static io.microsphere.util.ArrayUtils.EMPTY_BYTE_ARRAY;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link DecoratedRetryer} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedRetryer
 * @since 1.0.0
 */
class DecoratedRetryerTest extends DecoratedFeignComponentTest {

    private Retryer delegate;

    private DecoratedRetryer decoratedRetryer;

    @BeforeEach
    void setUp() {
        super.setUp();
        this.delegate = new Default();
        this.decoratedRetryer = instantiate(DecoratedRetryer.class, Retryer.class, this.contextId, this.contextFactory,
                this.clientProperties, null);
    }

    @Override
    protected Map<String, ApplicationContextInitializer<GenericApplicationContext>> applicationContextInitializers() {
        Map<String, ApplicationContextInitializer<GenericApplicationContext>> initializers = super.applicationContextInitializers();
        initializers.put(this.contextId, context -> {
            context.registerBean(Retryer.class, () -> delegate);
        });
        return initializers;
    }

    @Test
    void testComponentTypeFromDefaultConfiguration() {
        initDefaultConfiguration();
        this.decoratedRetryer.getDefaultConfiguration().setRetryer((Class) this.delegate.getClass());
        assertSame(this.delegate.getClass(), this.decoratedRetryer.componentType());
    }

    @Test
    void testComponentTypeFromCurrentConfiguration() {
        initCurrentConfiguration();
        this.decoratedRetryer.getCurrentConfiguration().setRetryer((Class) this.delegate.getClass());
        assertSame(this.delegate.getClass(), this.decoratedRetryer.componentType());
    }

    @Test
    void testComponentType() {
        assertSame(Retryer.class, this.decoratedRetryer.componentType());
    }

    @Test
    void testContinueOrPropagate() {
        Request request = create(Request.HttpMethod.GET, "http://localhost", emptyMap(), EMPTY_BYTE_ARRAY,
                null, null);
        RetryableException e = new RetryableException(1, "error", Request.HttpMethod.GET, new Date(), request);
        this.decoratedRetryer.continueOrPropagate(e);
        continueOrPropagate(null, e);
    }

    @Test
    void testClone() {
        assertNotNull(this.decoratedRetryer.clone());
    }
}