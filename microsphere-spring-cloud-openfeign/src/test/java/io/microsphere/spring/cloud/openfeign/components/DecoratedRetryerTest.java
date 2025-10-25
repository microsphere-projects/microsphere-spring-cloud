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
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;

import java.util.Date;

import static feign.Request.HttpMethod.GET;
import static io.microsphere.spring.cloud.openfeign.components.DecoratedRetryer.continueOrPropagate;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link DecoratedRetryer} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedRetryer
 * @since 1.0.0
 */
class DecoratedRetryerTest extends DecoratedFeignComponentTest<Retryer, DecoratedRetryer> {

    @Override
    protected Retryer createDelegate() {
        return new Default();
    }

    @Override
    protected void configureDelegateClass(FeignClientConfiguration configuration, Class<Retryer> delegateClass) {
        configuration.setRetryer(delegateClass);
    }

    @Test
    void testContinueOrPropagate() {
        this.decoratedComponent.refresh();
        Request request = createTestRequest();
        RetryableException e = new RetryableException(1, "error", GET, new Date(), request);
        this.decoratedComponent.continueOrPropagate(e);
        continueOrPropagate(null, e);
    }

    @Test
    void testClone() {
        assertNotNull(this.decoratedComponent.clone());
    }
}