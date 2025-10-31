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

package io.microsphere.spring.cloud.commons.condition;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static com.alibaba.spring.util.BeanUtils.isBeanPresent;
import static io.microsphere.spring.test.util.SpringTestUtils.testInSpringContainer;
import static java.lang.System.getProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link ConditionalOnUtilEnabled} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConditionalOnUtilEnabled
 * @since 1.0.0
 */
@ConditionalOnUtilEnabled
public class ConditionalOnUtilEnabledTest {

    @Test
    void testConfigBeanPresent() {
        testConfigBean(true);
    }

    @Test
    void testConfigBeanAbsent() {
        Properties properties = getProperties();
        try {
            properties.setProperty("spring.cloud.util.enabled", "false");
            testConfigBean(false);
        } finally {
            properties.remove("spring.cloud.util.enabled");
        }
    }

    void testConfigBean(boolean present) {
        testInSpringContainer(context -> {
            assertEquals(present, isBeanPresent(context, ConditionalOnUtilEnabledTest.class));
        }, ConditionalOnUtilEnabledTest.class);
    }
}
