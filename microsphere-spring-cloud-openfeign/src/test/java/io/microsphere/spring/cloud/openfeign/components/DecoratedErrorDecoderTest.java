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
import feign.codec.ErrorDecoder;
import feign.codec.ErrorDecoder.Default;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DecoratedErrorDecoder} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedErrorDecoder
 * @since 1.0.0
 */
class DecoratedErrorDecoderTest extends DecoratedFeignComponentTest<ErrorDecoder, DecoratedErrorDecoder> {
    @Override
    protected ErrorDecoder createDelegate() {
        return new Default();
    }

    @Override
    protected void configureDelegateClass(FeignClientConfiguration configuration, Class<ErrorDecoder> delegateClass) {
        configuration.setErrorDecoder(delegateClass);
    }

    @Test
    void testDecode() {
        Response response = createTestResponse();
        assertTrue(this.decoratedComponent.decode("echo", response) instanceof Exception);
    }
}