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

package io.microsphere.spring.cloud.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Properties;
import java.util.Set;

import static io.microsphere.collection.SetUtils.newLinkedHashSet;
import static io.microsphere.spring.beans.BeanUtils.isBeanPresent;
import static io.microsphere.spring.core.annotation.AnnotationUtils.getAnnotationAttributes;
import static io.microsphere.spring.test.util.SpringTestUtils.testInSpringContainer;
import static io.microsphere.util.ArrayUtils.isEmpty;
import static java.lang.String.valueOf;
import static java.lang.System.getProperties;
import static org.apache.commons.io.IOUtils.length;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.core.type.AnnotationMetadata.introspect;
import static org.springframework.util.StringUtils.hasText;

/**
 * Abstract test class for {@link ConditionalOnProperty @ConditionalOnProperty} On Enabled
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ConditionalOnProperty
 * @since 1.0.0
 */
public abstract class ConditionalOnPropertyEnabledTest {

    private AnnotationAttributes annotationAttributes;

    @BeforeEach
    void setUp() {
        AnnotationMetadata annotationMetadata = introspect(getClass());
        this.annotationAttributes = getAnnotationAttributes(annotationMetadata, ConditionalOnProperty.class);
    }

    @Test
    void testConditionalOnPropertyEnabled() {
        if (matchIfMissing()) {
            testBean(true);
        } else {
            testConditionalOnPropertyEnabled(true);
        }
    }

    @Test
    void testConditionalOnPropertyDisabled() {
        testConditionalOnPropertyEnabled(false);
    }

    /**
     * Whether match if missing
     *
     * @return {@code true} if match if missing
     */
    protected boolean matchIfMissing() {
        return this.annotationAttributes.getBoolean("matchIfMissing");
    }

    /**
     * Get the property names of the {@link Conditional @Conditional}
     *
     * @return property names
     */
    protected Set<String> getPropertyNames() {
        String prefix = this.annotationAttributes.getString("prefix");
        String[] names = this.annotationAttributes.getStringArray("name");
        if (isEmpty(names)) {
            names = this.annotationAttributes.getStringArray("value");
        }
        boolean hasPrefix = hasText(prefix);
        Set<String> propertyNames = newLinkedHashSet(length(names));
        for (String name : names) {
            String propertyName = hasPrefix ? prefix + name : name;
            propertyNames.add(propertyName);
        }
        return propertyNames;
    }

    protected void testConditionalOnPropertyEnabled(boolean enabled) {
        Set<String> propertyNames = getPropertyNames();
        Properties properties = getProperties();
        try {
            for (String propertyName : propertyNames) {
                properties.setProperty(propertyName, valueOf(enabled));
            }
            testBean(enabled);
        } finally {
            for (String propertyName : propertyNames) {
                properties.remove(propertyName);
            }
        }
    }

    protected void testBean(boolean present) {
        testInSpringContainer(context -> {
            assertEquals(present, isBeanPresent(context, getClass()));
        }, getClass());
    }
}