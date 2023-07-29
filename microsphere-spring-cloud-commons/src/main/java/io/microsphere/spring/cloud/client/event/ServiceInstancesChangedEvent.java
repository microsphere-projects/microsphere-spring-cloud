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
package io.microsphere.spring.cloud.client.event;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * An event raised after the {@link ServiceInstance instances} of one service has been
 * changed.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 */
public class ServiceInstancesChangedEvent extends ApplicationEvent {

    private final List<ServiceInstance> serviceInstances;

    /**
     * Current event has been processed or not. Typically, Spring Event was based on sync
     * {@link ApplicationEventMulticaster}
     *
     * @see SimpleApplicationEventMulticaster
     */
    private boolean processed = false;

    /**
     * @param serviceName      The name of service that was changed
     * @param serviceInstances all {@link ServiceInstance service instances}
     * @throws IllegalArgumentException if source is null.
     */
    public ServiceInstancesChangedEvent(String serviceName,
                                        List<ServiceInstance> serviceInstances) {
        super(serviceName);
        this.serviceInstances = unmodifiableList(serviceInstances);
    }

    /**
     * @return The name of service that was changed
     */
    public String getServiceName() {
        return (String) getSource();
    }

    /**
     * @return all {@link ServiceInstance service instances}.
     */
    public List<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    /**
     * Mark current event being processed.
     */
    public void processed() {
        processed = true;
    }

    /**
     * Current event has been processed or not.
     *
     * @return if processed, return <code>true</code>, or <code>false</code>
     */
    public boolean isProcessed() {
        return processed;
    }

}
