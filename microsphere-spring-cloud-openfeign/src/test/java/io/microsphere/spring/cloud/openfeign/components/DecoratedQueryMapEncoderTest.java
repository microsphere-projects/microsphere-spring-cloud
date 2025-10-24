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


import feign.QueryMapEncoder;
import feign.querymap.BeanQueryMapEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.microsphere.spring.cloud.openfeign.components.DecoratedFeignComponent.instantiate;
import static io.microsphere.spring.cloud.openfeign.components.DecoratedQueryMapEncoder.getQueryMapEncoder;
import static io.microsphere.spring.cloud.openfeign.components.DecoratedQueryMapEncoder.getQueryMapEncoderMethodHandle;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link DecoratedQueryMapEncoder} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedQueryMapEncoder
 * @since 1.0.0
 */
class DecoratedQueryMapEncoderTest extends DecoratedFeignComponentTest {

    private QueryMapEncoder delegate;

    private DecoratedQueryMapEncoder decoratedQueryMapEncoder;

    @BeforeEach
    void setUp() {
        super.setUp();
        this.delegate = new BeanQueryMapEncoder();
        this.decoratedQueryMapEncoder = instantiate(DecoratedQueryMapEncoder.class, QueryMapEncoder.class, this.contextId,
                this.contextFactory, this.clientProperties, this.delegate);
    }

    @Test
    void testComponentTypeFromDefaultConfiguration() {
        initDefaultConfiguration();
        this.decoratedQueryMapEncoder.getDefaultConfiguration().setQueryMapEncoder((Class) this.delegate.getClass());
        assertSame(this.delegate.getClass(), this.decoratedQueryMapEncoder.componentType());
    }

    @Test
    void testComponentTypeFromCurrentConfiguration() {
        initCurrentConfiguration();
        this.decoratedQueryMapEncoder.getCurrentConfiguration().setQueryMapEncoder((Class) this.delegate.getClass());
        assertSame(this.delegate.getClass(), this.decoratedQueryMapEncoder.componentType());
    }

    @Test
    void testComponentType() {
        assertSame(QueryMapEncoder.class, this.decoratedQueryMapEncoder.componentType());
    }

    @Test
    void testEncode() {
        assertSame(emptyMap(), this.decoratedQueryMapEncoder.encode(null));
    }

    @Test
    void testGetQueryMapEncoder() {
        assertNull(getQueryMapEncoder(null, null));
        assertNull(getQueryMapEncoder(getQueryMapEncoderMethodHandle, null));
    }
}