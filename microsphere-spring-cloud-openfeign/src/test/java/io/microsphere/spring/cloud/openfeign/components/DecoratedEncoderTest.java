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


import feign.RequestTemplate;
import feign.codec.Encoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.support.SpringEncoder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * {@link DecoratedEncoder} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedEncoder
 * @since 1.0.0
 */
class DecoratedEncoderTest extends DecoratedFeignComponentTest<Encoder, DecoratedEncoder> {

    @Override
    protected Encoder createDelegate() {
        HttpMessageConverters httpMessageConverters = new HttpMessageConverters();
        ObjectFactory<HttpMessageConverters> messageConverters = () -> httpMessageConverters;
        return new SpringEncoder(messageConverters);
    }

    @Override
    protected void configureDelegateClass(FeignClientConfiguration configuration, Class<Encoder> delegateClass) {
        configuration.setEncoder(delegateClass);
    }

    @Test
    void testEncode() {
        RequestTemplate template = new RequestTemplate();
        String value = "Test";
        this.decoratedComponent.encode(value, String.class, template);
        byte[] body = template.body();
        assertArrayEquals(value.getBytes(template.requestCharset()), body);
    }
}