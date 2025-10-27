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

package io.microsphere.spring.cloud.client.service.util;


import io.microsphere.spring.web.metadata.WebEndpointMapping;
import io.microsphere.spring.web.metadata.WebEndpointMapping.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collection;

import static io.microsphere.collection.Lists.ofList;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_CONTEXT_PATH_METADATA_NAME;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.attachMetadata;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.getMetadata;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.getWebEndpointMappings;
import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.parseWebEndpointMappings;
import static io.microsphere.spring.web.metadata.WebEndpointMapping.Kind.SERVLET;
import static io.microsphere.spring.web.metadata.WebEndpointMapping.servlet;
import static io.microsphere.util.StringUtils.EMPTY_STRING_ARRAY;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ServiceInstanceUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServiceInstanceUtils
 * @since 1.0.0
 */
class ServiceInstanceUtilsTest {

    private static final Integer WEB_ENDPOINT_MAPPING_ID = Integer.valueOf(12345);

    private static final String WEB_ENDPOINT_MAPPING_URL_PATTERN = "/test";

    private static final String WEB_ENDPOINT_MAPPING_METHOD = "GET";

    private String context = "/";

    private ServiceInstance serviceInstance;

    private Collection<WebEndpointMapping> webEndpointMappings;

    @BeforeEach
    void setUp() {
        this.serviceInstance = createDefaultServiceInstance();
        this.webEndpointMappings = createWebEndpointMappings();
    }

    @Test
    void testAttachMetadata() {
        attachMetadata(this.context, this.serviceInstance, this.webEndpointMappings);
        assertEquals(this.context, getMetadata(this.serviceInstance, WEB_CONTEXT_PATH_METADATA_NAME));
    }

    @Test
    void testGetWebEndpointMappings() {
        attachMetadata(this.context, this.serviceInstance, this.webEndpointMappings);
        Collection<WebEndpointMapping> webEndpointMappings = getWebEndpointMappings(this.serviceInstance);
        assertEquals(1, webEndpointMappings.size());
        WebEndpointMapping webEndpointMapping = webEndpointMappings.iterator().next();
        assertEquals(WEB_ENDPOINT_MAPPING_ID, webEndpointMapping.getId());
        assertTrue(webEndpointMapping.isNegated());
        assertEquals(WEB_ENDPOINT_MAPPING_URL_PATTERN, webEndpointMapping.getPatterns()[0]);
        assertEquals(WEB_ENDPOINT_MAPPING_METHOD, webEndpointMapping.getMethods()[0]);
        assertEquals(SERVLET, webEndpointMapping.getKind());
        assertArrayEquals(EMPTY_STRING_ARRAY, webEndpointMapping.getParams());
        assertArrayEquals(EMPTY_STRING_ARRAY, webEndpointMapping.getHeaders());
        assertArrayEquals(EMPTY_STRING_ARRAY, webEndpointMapping.getProduces());
        assertArrayEquals(EMPTY_STRING_ARRAY, webEndpointMapping.getConsumes());
    }

    @Test
    void testParseWebEndpointMappings() {
        assertSame(emptyList(), parseWebEndpointMappings(null));
        assertSame(emptyList(), parseWebEndpointMappings(""));
        assertSame(emptyList(), parseWebEndpointMappings(" "));
    }

    private Collection<WebEndpointMapping> createWebEndpointMappings() {
        return ofList(buildWebEndpointMapping(true));
    }

    private WebEndpointMapping buildWebEndpointMapping(boolean nagated) {
        Builder<?> builder = servlet()
                .endpoint(WEB_ENDPOINT_MAPPING_ID)
                .method(WEB_ENDPOINT_MAPPING_METHOD)
                .pattern(WEB_ENDPOINT_MAPPING_URL_PATTERN);
        if (nagated) {
            builder.negate();
        }
        return builder.build();
    }

    public static DefaultServiceInstance createDefaultServiceInstance() {
        DefaultServiceInstance serviceInstance = new DefaultServiceInstance();
        serviceInstance.setInstanceId("ServiceInstance-" + currentTimeMillis());
        serviceInstance.setServiceId("test-service");
        serviceInstance.setHost("localhost");
        serviceInstance.setPort(8080);
        serviceInstance.setSecure(false);
        return serviceInstance;
    }
}