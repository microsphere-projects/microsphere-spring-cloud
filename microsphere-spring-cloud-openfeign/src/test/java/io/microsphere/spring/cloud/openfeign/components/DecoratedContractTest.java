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


import feign.Contract;
import feign.MethodMetadata;
import io.microsphere.spring.cloud.openfeign.BaseClient;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClientProperties.FeignClientConfiguration;
import org.springframework.cloud.openfeign.support.SpringMvcContract;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link DecoratedContract} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedContract
 * @since 1.0.0
 */
class DecoratedContractTest extends DecoratedFeignComponentTest<Contract, DecoratedContract> {

    @Override
    protected Contract createDelegate() {
        return new SpringMvcContract();
    }

    @Override
    protected void configureDelegateClass(FeignClientConfiguration configuration, Class<Contract> delegateClass) {
        configuration.setContract(delegateClass);
    }

    @Test
    void testParseAndValidateMetadata() {
        assertMethodMetadataList(this.delegate.parseAndValidateMetadata(BaseClient.class),
                this.decoratedComponent.parseAndValidateMetadata(BaseClient.class));
    }

    void assertMethodMetadataList(List<MethodMetadata> one, List<MethodMetadata> another) {
        assertEquals(one.size(), another.size());
        for (int i = 0; i < one.size(); i++) {
            MethodMetadata oneMethodMetadata = one.get(i);
            MethodMetadata anotherMethodMetadata = another.get(i);
            assertMethodMetadata(oneMethodMetadata, anotherMethodMetadata);
        }
    }

    void assertMethodMetadata(MethodMetadata one, MethodMetadata another) {
        assertEquals(one.method(), another.method());
    }
}