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

package io.microsphere.spring.cloud.client.actuator;

import org.springframework.cloud.client.actuator.NamedFeature;

import java.util.Comparator;

/**
 * The {@link Comparator} class for {@link NamedFeature}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Comparator
 * @see NamedFeature
 * @since 1.0.0
 */
public class NamedFeatureComparator implements Comparator<NamedFeature> {

    public static final NamedFeatureComparator INSTANCE = new NamedFeatureComparator();

    @Override
    public int compare(NamedFeature namedFeature1, NamedFeature namedFeature2) {
        int c = namedFeature1.getName().compareTo(namedFeature2.getName());
        if (c == 0) {
            c = compare(namedFeature1.getType(), namedFeature2.getType());
        }
        return c;
    }

    private int compare(Class<?> type1, Class<?> type2) {
        return type1.getName().compareTo(type2.getName());
    }
}
