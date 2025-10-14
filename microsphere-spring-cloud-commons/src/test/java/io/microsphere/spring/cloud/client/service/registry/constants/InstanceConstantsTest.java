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

package io.microsphere.spring.cloud.client.service.registry.constants;


import org.junit.jupiter.api.Test;

import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.MANAGEMENT_PORT_METADATA_NAME;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.START_TIME_METADATA_NAME;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_CONTEXT_PATH_METADATA_NAME;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_MAPPINGS_METADATA_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link InstanceConstants} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see InstanceConstants
 * @since 1.0.0
 */
class InstanceConstantsTest {

    @Test
    void testConstants() {
        assertEquals("web.mappings", WEB_MAPPINGS_METADATA_NAME);
        assertEquals("web.context-path", WEB_CONTEXT_PATH_METADATA_NAME);
        assertEquals("management-port", MANAGEMENT_PORT_METADATA_NAME);
        assertEquals("start-time", START_TIME_METADATA_NAME);
    }
}