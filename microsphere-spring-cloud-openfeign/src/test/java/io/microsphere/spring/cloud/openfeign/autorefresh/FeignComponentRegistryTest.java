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
import io.microsphere.spring.cloud.openfeign.components.CompositedRequestInterceptor;
import io.microsphere.spring.cloud.openfeign.components.Refreshable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;

import static io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry.getComponentClass;
import static io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor.INSTANCE;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link FeignComponentRegistry} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see FeignComponentRegistry
 * @since 1.0.0
 */
class FeignComponentRegistryTest {

    private String clientName = "";

    private GenericApplicationContext context;

    private FeignComponentRegistry registry;

    @BeforeEach
    void setUp() {
        this.clientName = "test-client";
        this.context = new GenericApplicationContext();
        this.context.refresh();
        this.registry = new FeignComponentRegistry(this.clientName, this.context);
    }

    @Test
    void testGetComponentClass() {

        assertEquals(Retryer.class, getComponentClass("retryer"));

        assertEquals(ErrorDecoder.class, getComponentClass("error-decoder"));
        assertEquals(ErrorDecoder.class, getComponentClass("errorDecoder"));

        assertEquals(RequestInterceptor.class, getComponentClass("request-interceptors"));
        assertEquals(RequestInterceptor.class, getComponentClass("requestInterceptors"));

        assertEquals(RequestInterceptor.class, getComponentClass("default-request-headers"));
        assertEquals(RequestInterceptor.class, getComponentClass("defaultRequestHeaders"));

        assertEquals(RequestInterceptor.class, getComponentClass("default-query-parameters"));
        assertEquals(RequestInterceptor.class, getComponentClass("defaultQueryParameters"));

        assertEquals(Decoder.class, getComponentClass("decoder"));

        assertEquals(Encoder.class, getComponentClass("encoder"));

        assertEquals(Contract.class, getComponentClass("contract"));

        assertEquals(QueryMapEncoder.class, getComponentClass("query-map-encoder"));
        assertEquals(QueryMapEncoder.class, getComponentClass("queryMapEncoder"));

        assertNull(getComponentClass("unknown"));
        assertNull(getComponentClass(""));
        assertNull(getComponentClass(" "));
        assertNull(getComponentClass(null));
    }

    @Test
    void testGetComponentClassForMultipleConfigs() {

        assertEquals(Retryer.class, getComponentClass("retryer[0]"));

        assertEquals(ErrorDecoder.class, getComponentClass("error-decoder[0]"));
        assertEquals(ErrorDecoder.class, getComponentClass("errorDecoder[0]"));

        assertEquals(RequestInterceptor.class, getComponentClass("request-interceptors[0]"));
        assertEquals(RequestInterceptor.class, getComponentClass("requestInterceptors[0]"));

        assertEquals(RequestInterceptor.class, getComponentClass("default-request-headers[0]"));
        assertEquals(RequestInterceptor.class, getComponentClass("defaultRequestHeaders[0]"));

        assertEquals(RequestInterceptor.class, getComponentClass("default-query-parameters[0]"));
        assertEquals(RequestInterceptor.class, getComponentClass("defaultQueryParameters[0]"));

        assertEquals(Decoder.class, getComponentClass("decoder[0]"));

        assertEquals(Encoder.class, getComponentClass("encoder[0]"));

        assertEquals(Contract.class, getComponentClass("contract[0]"));

        assertEquals(QueryMapEncoder.class, getComponentClass("query-map-encoder[0]"));
        assertEquals(QueryMapEncoder.class, getComponentClass("queryMapEncoder[0]"));
    }

    @Test
    void testRegisterOnIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> this.registry.register(null, (Refreshable) null));
        assertThrows(IllegalArgumentException.class, () -> this.registry.register("", (Refreshable) null));
        assertThrows(IllegalArgumentException.class, () -> this.registry.register(" ", (Refreshable) null));
        assertThrows(IllegalArgumentException.class, () -> this.registry.register(this.clientName, (Refreshable) null));
        assertThrows(IllegalArgumentException.class, () -> this.registry.register(this.clientName, (List) null));
        assertThrows(IllegalArgumentException.class, () -> this.registry.register(this.clientName, emptyList()));
    }

    @Test
    void testRegisterRequestInterceptor() {
        assertTrue(this.registry.registerRequestInterceptor(this.clientName, INSTANCE) instanceof CompositedRequestInterceptor);
        assertSame(INSTANCE, this.registry.registerRequestInterceptor(this.clientName, INSTANCE));
    }

    @Test
    void testRegisterRequestInterceptorOnIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> this.registry.registerRequestInterceptor(null, null));
        assertThrows(IllegalArgumentException.class, () -> this.registry.registerRequestInterceptor("", null));
        assertThrows(IllegalArgumentException.class, () -> this.registry.registerRequestInterceptor(" ", null));
        assertThrows(IllegalArgumentException.class, () -> this.registry.registerRequestInterceptor(this.clientName, null));
    }

    @Test
    void testRefresh() {
        testRefresh(this.clientName);
        testRefresh("test-client-2");
    }

    void testRefresh(String clientName) {
        this.registry.refresh(clientName, "retryer");
        this.registry.refresh(clientName, "error-decoder", "decoder", "encoder");
        this.registry.refresh(clientName, "request-interceptors", "default-request-headers", "default-query-parameters");
    }
}