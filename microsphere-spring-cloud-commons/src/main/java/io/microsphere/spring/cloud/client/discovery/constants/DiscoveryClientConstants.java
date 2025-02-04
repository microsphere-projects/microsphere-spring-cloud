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
package io.microsphere.spring.cloud.client.discovery.constants;

import io.microsphere.spring.cloud.client.discovery.UnionDiscoveryClient;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;

import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX;

/**
 * The constants for {@link DiscoveryClient}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface DiscoveryClientConstants {

    /**
     * The property prefix of {@link DiscoveryClient} : "microsphere.spring.cloud.client.discovery."
     */
    String DISCOVERY_CLIENT_PROPERTY_PREFIX = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "client.discovery.";

    /**
     * The property name of mode : "mode"
     */
    String MODE_PROPERTY_NAME = "mode";

    /**
     * The {@link DiscoveryClient} "mode" for {@link UnionDiscoveryClient} : "union"
     */
    String UNION_DISCOVERY_CLIENT_MODE = "union";

    /**
     * The class name of {@link DiscoveryClient}
     */
    String DISCOVERY_CLIENT_CLASS_NAME = "org.springframework.cloud.client.discovery.DiscoveryClient";

    /**
     * The class name of {@link CompositeDiscoveryClient}
     */
    String COMPOSITE_DISCOVERY_CLIENT_CLASS_NAME = "org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient";

    /**
     * The class name of {@link CommonsClientAutoConfiguration}
     */
    String COMMONS_CLIENT_AUTO_CONFIGURATION_CLASS_NAME = "org.springframework.cloud.client.CommonsClientAutoConfiguration";

}
