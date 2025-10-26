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

package io.microsphere.spring.cloud.fault.tolerance.loadbalancer;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.microsphere.text.FormatUtils.format;
import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link WeightedRoundRobin} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see WeightedRoundRobin
 * @since 1.0.0
 */
class WeightedRoundRobinTest {

    private WeightedRoundRobin weightedRoundRobin;

    @BeforeEach
    void setUp() {
        this.weightedRoundRobin = new WeightedRoundRobin("test-id");
    }

    @Test
    void testGetId() {
        assertEquals("test-id", weightedRoundRobin.getId());
    }

    @Test
    void testWeight() {
        this.weightedRoundRobin.setWeight(10);
        assertEquals(10, this.weightedRoundRobin.getWeight());
    }

    @Test
    void testIncreaseCurrent() {
        this.weightedRoundRobin.setWeight(10);
        assertEquals(10L, this.weightedRoundRobin.increaseCurrent());
        assertEquals(20L, this.weightedRoundRobin.increaseCurrent());
        assertEquals(30L, this.weightedRoundRobin.increaseCurrent());
    }

    @Test
    void testSel() {
        this.weightedRoundRobin.sel(10);
        assertEquals(-10L, this.weightedRoundRobin.current.longValue());
    }

    @Test
    void testLastUpdate() {
        assertEquals(0L, this.weightedRoundRobin.getLastUpdate());
        long now = currentTimeMillis();
        this.weightedRoundRobin.setLastUpdate(now);
        assertEquals(now, this.weightedRoundRobin.getLastUpdate());
    }

    @Test
    void testToString() {
        this.weightedRoundRobin.setWeight(10);
        this.weightedRoundRobin.setLastUpdate(currentTimeMillis());
        assertEquals(format("WeightedRoundRobin[id='{}', weight={}, current={}, lastUpdate={}]",
                        this.weightedRoundRobin.getId(),
                        this.weightedRoundRobin.getWeight(),
                        this.weightedRoundRobin.current,
                        this.weightedRoundRobin.getLastUpdate()),
                this.weightedRoundRobin.toString());
    }
}