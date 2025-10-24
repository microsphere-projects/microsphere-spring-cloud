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


import feign.Response;
import feign.codec.Decoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link DecoratedDecoder} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedDecoder
 * @since 1.0.0
 */
class DecoratedDecoderTest extends DecoratedFeignComponentTest<Decoder, DecoratedDecoder> {

    @Override
    protected Decoder createDelegate() {
        HttpMessageConverters httpMessageConverters = new HttpMessageConverters();
        ObjectFactory<HttpMessageConverters> messageConverters = () -> httpMessageConverters;
        return new SpringDecoder(messageConverters);
    }

    @Override
    protected void configureDelegateClass(FeignClientConfiguration configuration, Class<Decoder> delegateClass) {
        configuration.setDecoder(delegateClass);
    }

    @Test
    void testDecode() throws IOException {
        Response response = createTestResponse();
        assertEquals(this.decoratedComponent.decode(response, String.class), this.delegate.decode(response, String.class));
    }
}