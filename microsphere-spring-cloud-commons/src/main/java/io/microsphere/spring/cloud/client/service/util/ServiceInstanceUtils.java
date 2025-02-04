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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.microsphere.logging.Logger;
import io.microsphere.logging.LoggerFactory;
import io.microsphere.spring.web.metadata.WebEndpointMapping;
import io.microsphere.util.BaseUtils;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static io.microsphere.net.URLUtils.decode;
import static io.microsphere.net.URLUtils.encode;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_CONTEXT_PATH_METADATA_NAME;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_MAPPINGS_METADATA_NAME;

/**
 * {@link ServiceInstance} Utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ServiceInstanceUtils extends BaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceUtils.class);

    public static void attachMetadata(String contextPath, ServiceInstance serviceInstance, Collection<WebEndpointMapping> webEndpointMappings) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        StringJoiner jsonBuilder = new StringJoiner(",", "[", "]");
        webEndpointMappings.stream().map(WebEndpointMapping::toJSON).forEach(jsonBuilder::add);
        String json = jsonBuilder.toString();
        metadata.put(WEB_CONTEXT_PATH_METADATA_NAME, contextPath);
        try {
            String encodedJson = encode(json);
            metadata.put(WEB_MAPPINGS_METADATA_NAME, encodedJson);
        } catch (IllegalArgumentException e) {
            logger.error("The JSON content of WebEndpointMappings can't be encoded : {}", json, e);
        }
    }

    public static Collection<WebEndpointMapping> getWebEndpointMappings(ServiceInstance serviceInstance) {
        List<WebEndpointMapping> webEndpointMappings = Collections.emptyList();
        Map<String, String> metadata = serviceInstance.getMetadata();
        String encodedJSON = metadata.get(WEB_MAPPINGS_METADATA_NAME);
        if (encodedJSON != null) {
            try {
                String json = decode(encodedJSON);
                ObjectMapper objectMapper = new ObjectMapper();
                webEndpointMappings = objectMapper.readValue(json, new TypeReference<List<WebEndpointMapping>>() {
                });
            } catch (Throwable e) {
                logger.error("The encoded JSON content of WebEndpointMappings can't be parsed : {}", encodedJSON, e);
            }
        }
        return webEndpointMappings;
    }
}
