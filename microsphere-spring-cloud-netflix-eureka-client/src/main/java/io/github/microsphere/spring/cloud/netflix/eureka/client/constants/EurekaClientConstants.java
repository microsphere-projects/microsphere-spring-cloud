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
package io.github.microsphere.spring.cloud.netflix.eureka.client.constants;

import com.netflix.discovery.EurekaClient;

/**
 * The constants for {@link EurekaClient}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface EurekaClientConstants {

    /**
     * The property prefix of {@link EurekaClient}
     */
    String EUREKA_CLIENT_PROPERTY_PREFIX = "microsphere.eureka.client";

    /**
     * The property name of "enabled"
     */
    String ENABLED_PROPERTY_NAME = "enabled";

    /**
     * The class name of {@link EurekaClient}
     */
    String EUREKA_CLIENT_CLASS_NAME = "com.netflix.discovery.EurekaClient";

}
