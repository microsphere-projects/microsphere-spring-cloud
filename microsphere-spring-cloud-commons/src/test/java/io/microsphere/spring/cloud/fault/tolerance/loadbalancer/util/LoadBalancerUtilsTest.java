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

package io.microsphere.spring.cloud.fault.tolerance.loadbalancer.util;


import org.junit.jupiter.api.Test;

import static io.microsphere.spring.cloud.fault.tolerance.loadbalancer.util.LoadBalancerUtils.calculateWarmupWeight;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link LoadBalancerUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see LoadBalancerUtils
 * @since 1.0.0
 */
class LoadBalancerUtilsTest {

    @Test
    void testCalculateWarmupWeight() throws InterruptedException {
        long uptime = currentTimeMillis();
        int weight = 10;
        for (int i = 0; i < 10; i++) {
            assertTrue(weight <= (weight = calculateWarmupWeight(uptime, uptime, weight)));
            sleep(10);
        }
    }
}