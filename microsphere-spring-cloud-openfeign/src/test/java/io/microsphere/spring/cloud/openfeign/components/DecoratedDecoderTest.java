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
import feign.Response;
import feign.codec.Decoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.io.IOException;

import static feign.Request.create;
import static feign.Response.builder;
import static io.microsphere.spring.cloud.openfeign.components.DecoratedFeignComponent.instantiate;
import static io.microsphere.util.ArrayUtils.EMPTY_BYTE_ARRAY;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link DecoratedDecoder} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedDecoder
 * @since 1.0.0
 */
class DecoratedDecoderTest extends DecoratedFeignComponentTest {

    private Decoder delegate;

    private DecoratedDecoder decoratedDecoder;

    @BeforeEach
    void setUp() {
        super.setUp();
        HttpMessageConverters httpMessageConverters = new HttpMessageConverters();
        ObjectFactory<HttpMessageConverters> messageConverters = () -> httpMessageConverters;
        this.delegate = new SpringDecoder(messageConverters);
        this.decoratedDecoder = instantiate(DecoratedDecoder.class, Decoder.class, contextId, contextFactory,
                clientProperties, delegate);
    }

    @Test
    void testComponentTypeFromDefaultConfiguration() {
        initDefaultConfiguration();
        this.decoratedDecoder.getDefaultConfiguration().setDecoder((Class) this.delegate.getClass());
        assertSame(this.delegate.getClass(), this.decoratedDecoder.componentType());
    }

    @Test
    void testComponentTypeFromCurrentConfiguration() {
        initCurrentConfiguration();
        this.decoratedDecoder.getCurrentConfiguration().setDecoder((Class) this.delegate.getClass());
        assertSame(this.delegate.getClass(), this.decoratedDecoder.componentType());
    }

    @Test
    void testComponentType() {
        assertSame(Decoder.class, this.decoratedDecoder.componentType());
    }

    @Test
    void testDecode() throws IOException {
        Request request = create(Request.HttpMethod.GET, "http://localhost", emptyMap(), EMPTY_BYTE_ARRAY,
                null, null);

        Response response = builder()
                .status(200)
                .request(request)
                .body(new byte[1024])
                .build();

        assertEquals(this.decoratedDecoder.decode(response, String.class), this.delegate.decode(response, String.class));
    }
}