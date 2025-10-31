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
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static io.microsphere.collection.ListUtils.newArrayList;
import static io.microsphere.constants.SeparatorConstants.LINE_SEPARATOR;
import static io.microsphere.constants.SymbolConstants.COLON_CHAR;
import static io.microsphere.constants.SymbolConstants.COMMA;
import static io.microsphere.constants.SymbolConstants.LEFT_SQUARE_BRACKET;
import static io.microsphere.constants.SymbolConstants.RIGHT_SQUARE_BRACKET;
import static io.microsphere.json.JSONUtils.jsonArray;
import static io.microsphere.json.JSONUtils.readArray;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.net.URLUtils.decode;
import static io.microsphere.net.URLUtils.encode;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_CONTEXT_PATH_METADATA_NAME;
import static io.microsphere.spring.cloud.client.service.registry.constants.InstanceConstants.WEB_MAPPINGS_METADATA_NAME;
import static io.microsphere.spring.web.metadata.WebEndpointMapping.Kind.valueOf;
import static io.microsphere.spring.web.metadata.WebEndpointMapping.of;
import static io.microsphere.util.StringUtils.EMPTY_STRING;
import static io.microsphere.util.StringUtils.EMPTY_STRING_ARRAY;
import static io.microsphere.util.StringUtils.isBlank;
import static java.lang.String.valueOf;
import static java.net.URI.create;
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
        StringJoiner jsonBuilder = new StringJoiner(COMMA + LINE_SEPARATOR, LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET);
        webEndpointMappings.stream().map(WebEndpointMapping::toJSON).forEach(jsonBuilder::add);
        String json = jsonBuilder.toString();
        logger.trace("Web Endpoint Mappings JSON: \n{}", json);
        json = json.replace(LINE_SEPARATOR, EMPTY_STRING);
        String encodedJson = encode(json);

        metadata.put(WEB_CONTEXT_PATH_METADATA_NAME, contextPath);
        metadata.put(WEB_MAPPINGS_METADATA_NAME, encodedJson);
        logger.trace("ServiceInstance's metadata :");
        metadata.forEach((name, value) -> logger.trace("{} : {}", name, value));
    }

    /**
     * Get {@link WebEndpointMapping}s from {@link ServiceInstance}
     *
     * @param serviceInstance {@link ServiceInstance}
     * @return {@link WebEndpointMapping}s
     */
    @Nonnull
    public static Collection<WebEndpointMapping> getWebEndpointMappings(ServiceInstance serviceInstance) {
        String encodedJSON = getMetadata(serviceInstance, WEB_MAPPINGS_METADATA_NAME);
        return parseWebEndpointMappings(encodedJSON);
    }

    /**
     * Get the String representation of {@link ServiceInstance#getUri()}
     *
     * @param instance {@link ServiceInstance}
     * @return the String representation of {@link ServiceInstance#getUri()}
     */
    @Nonnull
    public static String getUriString(ServiceInstance instance) {
        boolean isSecure = instance.isSecure();
        String prefix = isSecure ? "https://" : "http://";
        String host = instance.getHost();
        int port = instance.getPort();
        if (port <= 0) {
            port = isSecure ? 443 : 80;
        }
        String portString = valueOf(port);
        StringBuilder urlStringBuilder = new StringBuilder((isSecure ? 9 : 8) + host.length() + portString.length());
        urlStringBuilder.append(prefix)
                .append(host)
                .append(COLON_CHAR)
                .append(portString);
        return urlStringBuilder.toString();
    }

    /**
     * Alternative method of {@link ServiceInstance#getUri()} with the better performance
     *
     * @param serviceInstance {@link ServiceInstance}
     * @return {@link URI} instance
     * @see DefaultServiceInstance#getUri(ServiceInstance)
     */
    @Nonnull
    public static URI getUri(ServiceInstance serviceInstance) {
        String uriString = getUriString(serviceInstance);
        return create(uriString);
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

    /**
     * Set metadata by metadataName
     *
     * @param serviceInstance {@link ServiceInstance}
     * @param metadataName    metadataName
     * @param metadataValue   metadata value
     * @return the previous value associated with <code>metadataName</code> if found, <code>null</code> otherwise
     */
    public static String setMetadata(ServiceInstance serviceInstance, String metadataName, String metadataValue) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        return metadata.put(metadataName, metadataValue);
    }

    /**
     * Remove metadata by metadataName
     *
     * @param serviceInstance {@link ServiceInstance}
     * @param metadataName    metadataName
     * @return the value associated with <code>metadataName</code> if found, <code>null</code> otherwise
     */
    public static String removeMetadata(ServiceInstance serviceInstance, String metadataName) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        return metadata.remove(metadataName);
    }

    /**
     * Set properties from source to target
     *
     * @param source source {@link ServiceInstance}
     * @param target target {@link DefaultServiceInstance}
     */
    public static void setProperties(ServiceInstance source, DefaultServiceInstance target) {
        target.setInstanceId(source.getInstanceId());
        target.setServiceId(source.getServiceId());
        URI uri = getUri(source);
        target.setUri(uri);
        Map<String, String> metadata = source.getMetadata();
        metadata.clear();
        metadata.putAll(source.getMetadata());
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

    private ServiceInstanceUtils() {
    }
}