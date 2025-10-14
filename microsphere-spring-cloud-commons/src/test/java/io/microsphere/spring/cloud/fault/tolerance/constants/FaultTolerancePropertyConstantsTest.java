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

package io.microsphere.spring.cloud.fault.tolerance.constants;


import org.junit.jupiter.api.Test;

import static io.microsphere.spring.cloud.fault.tolerance.constants.FaultTolerancePropertyConstants.DEFAULT_WARMUP_TIME_PROPERTY_VALUE;
import static io.microsphere.spring.cloud.fault.tolerance.constants.FaultTolerancePropertyConstants.DEFAULT_WEIGHT_PROPERTY_VALUE;
import static io.microsphere.spring.cloud.fault.tolerance.constants.FaultTolerancePropertyConstants.FAULT_TOLERANCE_PROPERTY_NAME_PREFIX;
import static io.microsphere.spring.cloud.fault.tolerance.constants.FaultTolerancePropertyConstants.LOAD_BALANCER_PROPERTY_PREFIX;
import static io.microsphere.spring.cloud.fault.tolerance.constants.FaultTolerancePropertyConstants.WARMUP_TIME_PROPERTY_NAME;
import static io.microsphere.spring.cloud.fault.tolerance.constants.FaultTolerancePropertyConstants.WEIGHT_PROPERTY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link FaultTolerancePropertyConstants} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see FaultTolerancePropertyConstants
 * @since 1.0.0
 */
class FaultTolerancePropertyConstantsTest {

    @Test
    void testConstants() {
        assertEquals("microsphere.spring.cloud.fault-tolerance.", FAULT_TOLERANCE_PROPERTY_NAME_PREFIX);
        assertEquals("microsphere.spring.cloud.fault-tolerance.load-balancer.", LOAD_BALANCER_PROPERTY_PREFIX);
        assertEquals("microsphere.spring.cloud.fault-tolerance.warmup-time", WARMUP_TIME_PROPERTY_NAME);
        assertEquals("microsphere.spring.cloud.fault-tolerance.weight", WEIGHT_PROPERTY_NAME);
        assertEquals(600000, DEFAULT_WARMUP_TIME_PROPERTY_VALUE);
        assertEquals(100, DEFAULT_WEIGHT_PROPERTY_VALUE);
    }

}