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

package io.microsphere.spring.cloud.context.named;


import io.microsphere.spring.cloud.context.named.config.SpecificationBeanPostProcessor;
import org.springframework.cloud.context.named.NamedContextFactory.Specification;

/**
 * The Customizer of {@link Specification}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Specification
 * @see SpecificationBeanPostProcessor
 * @since 1.0.0
 */
public interface SpecificationCustomizer {

    /**
     * Customizes {@link Specification} bean and bean name
     *
     * @param specification {@link Specification}
     * @param beanName      the bean name of {@link Specification}
     */
    void customize(Specification specification, String beanName);
}