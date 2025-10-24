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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.support.SpringMvcContract;

import java.util.List;

import static io.microsphere.spring.cloud.openfeign.components.DecoratedFeignComponent.instantiate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link DecoratedContract} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedContract
 * @since 1.0.0
 */
class DecoratedContractTest extends DecoratedFeignComponentTest {

    private Contract delegate;

    private DecoratedContract decoratedContract;

    @BeforeEach
    void setUp() {
        super.setUp();
        this.delegate = new SpringMvcContract();
        this.decoratedContract = instantiate(DecoratedContract.class, Contract.class, this.contextId,
                this.contextFactory, this.clientProperties, this.delegate);
    }

    @Test
    void testComponentTypeFromDefaultConfiguration() {
        initDefaultConfiguration();
        this.decoratedContract.getDefaultConfiguration().setContract((Class) this.delegate.getClass());
        assertSame(this.delegate.getClass(), this.decoratedContract.componentType());
    }

    @Test
    void testComponentTypeFromCurrentConfiguration() {
        initCurrentConfiguration();
        this.decoratedContract.getCurrentConfiguration().setContract((Class) this.delegate.getClass());
        assertSame(this.delegate.getClass(), this.decoratedContract.componentType());
    }

    @Test
    void testComponentType() {
        assertSame(Contract.class, this.decoratedContract.componentType());
    }

    @Test
    void testParseAndValidateMetadata() {
        assertMethodMetadataList(delegate.parseAndValidateMetadata(BaseClient.class),
                decoratedContract.parseAndValidateMetadata(BaseClient.class));
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