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

package io.microsphere.spring.cloud.client.service.registry;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static io.microsphere.collection.Lists.ofList;
import static io.microsphere.collection.MapUtils.ofEntry;
import static io.microsphere.collection.Maps.ofMap;
import static io.microsphere.spring.cloud.client.service.registry.MultipleRegistrationTest.createDefaultRegistration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link RegistrationMetaData} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see RegistrationMetaData
 * @since 1.0.0
 */
class RegistrationMetaDataTest {

    private DefaultRegistration defaultRegistration;

    private RegistrationMetaData metaData;

    @BeforeEach
    void setUp() {
        this.defaultRegistration = createDefaultRegistration();
        Map<String, String> metadata = this.defaultRegistration.getMetadata();
        metadata.put("key1", "value1");
        metadata.put("key2", "value2");
        metadata.put("key3", "value3");
        this.metaData = new RegistrationMetaData(ofList(defaultRegistration));
    }

    @Test
    void testSize() {
        assertEquals(3, metaData.size());
    }

    @Test
    void testIsEmpty() {
        assertEquals(false, metaData.isEmpty());
    }

    @Test
    void testContainsKey() {
        assertTrue(metaData.containsKey("key1"));
        assertFalse(metaData.containsKey("key4"));
    }

    @Test
    void testContainsValue() {
        assertTrue(metaData.containsValue("value1"));
        assertFalse(metaData.containsValue("value4"));
    }

    @Test
    void testGet() {
        assertEquals("value1", metaData.get("key1"));
        assertEquals("value2", metaData.get("key2"));
        assertEquals("value3", metaData.get("key3"));
        assertNull(metaData.get("key4"));
    }

    @Test
    void testPut() {
        metaData.put("key4", "value4");
        assertEquals("value4", metaData.get("key4"));
    }

    @Test
    void testRemove() {
        metaData.remove("key1");
        assertNull(metaData.get("key1"));
    }

    @Test
    void testPutAll() {
        metaData.putAll(ofMap("key4", "value4", "key5", "value5"));
        assertEquals("value1", metaData.get("key1"));
        assertEquals("value2", metaData.get("key2"));
        assertEquals("value3", metaData.get("key3"));
        assertEquals("value4", metaData.get("key4"));
        assertEquals("value5", metaData.get("key5"));
    }

    @Test
    void testClear() {
        metaData.clear();
        assertEquals(0, metaData.size());
    }

    @Test
    void testKeySet() {
        Set<String> keys = metaData.keySet();
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
        assertTrue(keys.contains("key3"));
        assertFalse(keys.contains("key4"));
    }

    @Test
    void testValues() {
        Collection<String> values = metaData.values();
        assertTrue(values.contains("value1"));
        assertTrue(values.contains("value2"));
        assertTrue(values.contains("value3"));
        assertFalse(values.contains("value4"));
    }

    @Test
    void testEntrySet() {
        Set<Map.Entry<String, String>> entries = metaData.entrySet();
        assertTrue(entries.contains(ofEntry("key1", "value1")));
        assertTrue(entries.contains(ofEntry("key2", "value2")));
        assertTrue(entries.contains(ofEntry("key3", "value3")));
        assertFalse(entries.contains(ofEntry("key4", "value4")));
    }
}