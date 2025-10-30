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

package io.microsphere.spring.cloud.client.discovery;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * An adapter {@link DiscoveryClient} class based on {@link ReactiveDiscoveryClient}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DiscoveryClient
 * @since 1.0.0
 */
public class ReactiveDiscoveryClientAdapter implements DiscoveryClient {

    private final ReactiveDiscoveryClient reactiveDiscoveryClient;

    public ReactiveDiscoveryClientAdapter(ReactiveDiscoveryClient reactiveDiscoveryClient) {
        this.reactiveDiscoveryClient = reactiveDiscoveryClient;
    }

    @Override
    public String description() {
        return this.reactiveDiscoveryClient.description();
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        Flux<ServiceInstance> flux = this.reactiveDiscoveryClient.getInstances(serviceId);
        return toList(flux);
    }

    @Override
    public List<String> getServices() {
        Flux<String> flux = this.reactiveDiscoveryClient.getServices();
        return toList(flux);
    }

    @Override
    public void probe() {
        this.reactiveDiscoveryClient.probe();
    }

    @Override
    public int getOrder() {
        return this.reactiveDiscoveryClient.getOrder();
    }

    static <T> List<T> toList(Flux<T> flux) {
        return flux.collectList().block();
    }
}