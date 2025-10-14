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

import io.microsphere.annotation.Nonnull;
import io.microsphere.annotation.Nullable;
import io.microsphere.json.JSONArray;
import io.microsphere.json.JSONObject;
import io.microsphere.logging.Logger;
import io.microsphere.spring.web.metadata.WebEndpointMapping;
import io.microsphere.spring.web.metadata.WebEndpointMapping.Builder;
import io.microsphere.util.BaseUtils;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static io.microsphere.collection.ListUtils.newArrayList;
import static io.microsphere.constants.SeparatorConstants.LINE_SEPARATOR;
import static io.microsphere.constants.SymbolConstants.COLON;
import static io.microsphere.constants.SymbolConstants.COMMA;
import static io.microsphere.constants.SymbolConstants.DOUBLE_QUOTE;
import static io.microsphere.constants.SymbolConstants.RIGHT_CURLY_BRACE;
import static io.microsphere.json.JSONUtils.jsonArray;
import static io.microsphere.json.JSONUtils.readArray;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.net.URLUtils.decode;
import static io.microsphere.net.URLUtils.encode;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_CONTEXT_PATH_METADATA_NAME;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_MAPPINGS_METADATA_NAME;
import static io.microsphere.spring.web.metadata.WebEndpointMapping.Kind.valueOf;
import static io.microsphere.spring.web.metadata.WebEndpointMapping.of;
import static io.microsphere.util.StringUtils.EMPTY_STRING_ARRAY;
import static io.microsphere.util.StringUtils.isBlank;
import static java.util.Collections.emptyList;

/**
 * {@link ServiceInstance} Utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ServiceInstanceUtils extends BaseUtils {

    private static final Logger logger = getLogger(ServiceInstanceUtils.class);

    public static void attachMetadata(String contextPath, ServiceInstance serviceInstance, Collection<WebEndpointMapping> webEndpointMappings) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        StringJoiner jsonBuilder = new StringJoiner(",", "[", "]");
        webEndpointMappings.stream().map(mapping -> toJSON(mapping)).forEach(jsonBuilder::add);
        String json = jsonBuilder.toString();
        metadata.put(WEB_CONTEXT_PATH_METADATA_NAME, contextPath);
        String encodedJson = encode(json);
        metadata.put(WEB_MAPPINGS_METADATA_NAME, encodedJson);
    }

    /**
     * Get {@link WebEndpointMapping}s from {@link ServiceInstance}
     *
     * @param serviceInstance {@link ServiceInstance}
     * @return {@link WebEndpointMapping}s
     */
    @Nonnull
    public static Collection<WebEndpointMapping> getWebEndpointMappings(ServiceInstance serviceInstance) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        String encodedJSON = metadata.get(WEB_MAPPINGS_METADATA_NAME);
        return parseWebEndpointMappings(encodedJSON);
    }

    static String toJSON(WebEndpointMapping webEndpointMapping) {
        // FIXME : Issue on WebEndpointMapping.toJSON()
        String json = webEndpointMapping.toJSON();
        StringBuilder jsonBuilder = new StringBuilder(json);
        int startIndex = jsonBuilder.lastIndexOf(LINE_SEPARATOR);
        int endIndex = jsonBuilder.indexOf(RIGHT_CURLY_BRACE);
        String kindItem = COMMA + LINE_SEPARATOR + DOUBLE_QUOTE + "kind" + DOUBLE_QUOTE + COLON +
                DOUBLE_QUOTE + webEndpointMapping.getKind() + DOUBLE_QUOTE + LINE_SEPARATOR;
        jsonBuilder.replace(startIndex, endIndex, kindItem);
        return jsonBuilder.toString();
    }

    static List<WebEndpointMapping> parseWebEndpointMappings(String encodedJSON) {
        if (isBlank(encodedJSON)) {
            return emptyList();
        }
        String json = decode(encodedJSON);
        JSONArray jsonArray = jsonArray(json);
        int size = jsonArray.length();
        List<WebEndpointMapping> webEndpointMappings = newArrayList(size);
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            WebEndpointMapping webEndpointMapping = parseWebEndpointMapping(jsonObject);
            webEndpointMappings.add(webEndpointMapping);
        }
        return webEndpointMappings;
    }

    static WebEndpointMapping parseWebEndpointMapping(JSONObject jsonObject) {
        String kind = jsonObject.optString("kind");
        int id = jsonObject.optInt("id");
        boolean negated = jsonObject.optBoolean("negated");
        String[] patterns = getArray(jsonObject, "patterns");
        String[] methods = getArray(jsonObject, "methods");
        String[] params = getArray(jsonObject, "params");
        String[] headers = getArray(jsonObject, "headers");
        String[] consumes = getArray(jsonObject, "consumes");
        String[] produces = getArray(jsonObject, "produces");
        Builder<?> builder = of(valueOf(kind))
                .endpoint(Integer.valueOf(id))
                .patterns(patterns)
                .methods(methods)
                .params(params)
                .headers(headers)
                .consumes(consumes)
                .produces(produces);
        if (negated) {
            builder.negate();
        }
        return builder.build();
    }

    static String[] getArray(JSONObject jsonObject, String name) {
        JSONArray jsonArray = jsonObject.optJSONArray(name);
        return jsonArray == null ? EMPTY_STRING_ARRAY : readArray(jsonArray, String.class);
    }

    /**
     * Get metadata by metadataName
     *
     * @param serviceInstance {@link ServiceInstance}
     * @param metadataName    metadataName
     * @return metadata value
     */
    @Nullable
    public static String getMetadata(ServiceInstance serviceInstance, String metadataName) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        return metadata.get(metadataName);
    }
    
    private ServiceInstanceUtils() {
    }
}
