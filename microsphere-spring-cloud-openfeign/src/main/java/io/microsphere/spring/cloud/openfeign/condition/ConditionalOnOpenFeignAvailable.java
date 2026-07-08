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
package io.microsphere.spring.cloud.openfeign.condition;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static io.microsphere.spring.cloud.openfeign.constants.FeignConstants.FEIGN_CLASS_NAME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link org.springframework.context.annotation.Conditional} that only matches when OpenFeign is enabled
 * and the Feign classes are available on the classpath.
 *
 * <h3>Example Usage</h3>
 * <h4>Example 1:</h4> Conditionally enable a configuration class.
 * <pre>{@code
 * @Configuration
 * @ConditionalOnOpenFeignAvailable
 * public class MyOpenFeignConfiguration {
 *     // Configuration beans
 * }
 * }</pre>
 *
 * <h4>Example 2:</h4> Conditionally register a bean.
 * <pre>{@code
 * @Bean
 * @ConditionalOnOpenFeignAvailable
 * public MyService myService() {
 *     return new MyService();
 * }
 * }</pre>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConditionalOnOpenFeignEnabled
 * @see ConditionalOnClass
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Documented
@ConditionalOnOpenFeignEnabled
@ConditionalOnClass(name = {
        FEIGN_CLASS_NAME
})
public @interface ConditionalOnOpenFeignAvailable {
}