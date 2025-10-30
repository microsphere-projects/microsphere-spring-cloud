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

    @Override
    public void register(Registration registration) {
        String id = registration.getInstanceId();
        storage.put(id, registration);
    }

    @Override
    public void deregister(Registration registration) {
        String id = registration.getInstanceId();
        storage.remove(id, registration);
    }

    @Override
    public void close() {
        storage.clear();
    }

    @Override
    public void setStatus(Registration registration, String status) {
        Map<String, String> metadata = getMetadata(registration);
        if (metadata != null) {
            metadata.put(STATUS_KEY, status);
        }
    }

    @Override
    public Object getStatus(Registration registration) {
        Map<String, String> metadata = getMetadata(registration);
        if (metadata != null) {
            return metadata.get(STATUS_KEY);
        }
        return null;
    }

    protected Map<String, String> getMetadata(Registration registration) {
        String id = registration.getInstanceId();
        Registration instance = storage.get(id);
        if (instance != null) {
            return instance.getMetadata();
        }
        return null;
    }
}