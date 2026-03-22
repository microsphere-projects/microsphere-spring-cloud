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
package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-Memory {@link ServiceRegistry}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InMemoryServiceRegistry implements ServiceRegistry {

    private static final String STATUS_KEY = "_status_";

    private final ConcurrentMap<String, Registration> storage = new ConcurrentHashMap<>(1);

    /**
     * Registers the given {@link Registration} by storing it in memory keyed by its instance ID.
     *
     * <p>Example Usage:
     * <pre>{@code
     * InMemoryServiceRegistry registry = new InMemoryServiceRegistry();
     * registry.register(registration);
     * }</pre>
     *
     * @param registration the {@link Registration} to register
     */
    @Override
    public void register(Registration registration) {
        String id = registration.getInstanceId();
        storage.put(id, registration);
    }

    /**
     * Deregisters the given {@link Registration} by removing it from in-memory storage.
     *
     * <p>Example Usage:
     * <pre>{@code
     * registry.deregister(registration);
     * }</pre>
     *
     * @param registration the {@link Registration} to deregister
     */
    @Override
    public void deregister(Registration registration) {
        String id = registration.getInstanceId();
        storage.remove(id, registration);
    }

    /**
     * Closes this registry by clearing all stored registrations.
     *
     * <p>Example Usage:
     * <pre>{@code
     * registry.close();
     * }</pre>
     */
    @Override
    public void close() {
        storage.clear();
    }

    /**
     * Sets the status of the given {@link Registration} by storing it in the registration's metadata.
     *
     * <p>Example Usage:
     * <pre>{@code
     * registry.setStatus(registration, "UP");
     * }</pre>
     *
     * @param registration the {@link Registration} whose status is to be set
     * @param status       the status value to set
     */
    @Override
    public void setStatus(Registration registration, String status) {
        Map<String, String> metadata = getMetadata(registration);
        if (metadata != null) {
            metadata.put(STATUS_KEY, status);
        }
    }

    /**
     * Returns the status of the given {@link Registration} from its metadata, or {@code null}
     * if the registration is not found.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Object status = registry.getStatus(registration);
     * }</pre>
     *
     * @param registration the {@link Registration} whose status is to be retrieved
     * @return the status value, or {@code null} if not found
     */
    @Override
    public Object getStatus(Registration registration) {
        Map<String, String> metadata = getMetadata(registration);
        if (metadata != null) {
            return metadata.get(STATUS_KEY);
        }
        return null;
    }

    /**
     * Retrieves the metadata {@link Map} for the given {@link Registration} from in-memory storage.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Map<String, String> metadata = registry.getMetadata(registration);
     * }</pre>
     *
     * @param registration the {@link Registration} whose metadata is to be retrieved
     * @return the metadata map, or {@code null} if the registration is not stored
     */
    protected Map<String, String> getMetadata(Registration registration) {
        String id = registration.getInstanceId();
        Registration instance = storage.get(id);
        if (instance != null) {
            return instance.getMetadata();
        }
        return null;
    }
}