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
import reactor.core.publisher.Mono;

import java.util.List;

import static io.microsphere.lang.function.ThrowableSupplier.execute;
import static reactor.core.scheduler.Schedulers.isInNonBlockingThread;

/**
 * An adapter {@link DiscoveryClient} class based on {@link ReactiveDiscoveryClient}
 *
 * <p>Example Usage:
 * <pre>{@code
 * ReactiveDiscoveryClientAdapter adapter = new ReactiveDiscoveryClientAdapter(reactiveDiscoveryClient);
 * List<ServiceInstance> instances = adapter.getInstances("test-service");
 * List<String> services = adapter.getServices();
 * }</pre>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DiscoveryClient
 * @since 1.0.0
 */
public class ReactiveDiscoveryClientAdapter implements DiscoveryClient {

    private final ReactiveDiscoveryClient reactiveDiscoveryClient;

    /**
     * Constructs a new {@link ReactiveDiscoveryClientAdapter} wrapping the given
     * {@link ReactiveDiscoveryClient}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * ReactiveDiscoveryClientAdapter adapter = new ReactiveDiscoveryClientAdapter(reactiveDiscoveryClient);
     * }</pre>
     *
     * @param reactiveDiscoveryClient the {@link ReactiveDiscoveryClient} to adapt
     */
    public ReactiveDiscoveryClientAdapter(ReactiveDiscoveryClient reactiveDiscoveryClient) {
        this.reactiveDiscoveryClient = reactiveDiscoveryClient;
    }

    /**
     * Delegates to the underlying {@link ReactiveDiscoveryClient#description()} method.
     *
     * <p>Example Usage:
     * <pre>{@code
     * String desc = adapter.description();
     * }</pre>
     *
     * @return the description from the underlying {@link ReactiveDiscoveryClient}
     */
    @Override
    public String description() {
        return this.reactiveDiscoveryClient.description();
    }

    /**
     * Returns a blocking list of {@link ServiceInstance} objects for the given service ID
     * by collecting results from the underlying {@link ReactiveDiscoveryClient}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * List<ServiceInstance> instances = adapter.getInstances("test-service");
     * }</pre>
     *
     * @param serviceId the service ID to look up
     * @return the list of {@link ServiceInstance} from the reactive client
     */
    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        Flux<ServiceInstance> flux = this.reactiveDiscoveryClient.getInstances(serviceId);
        return toList(flux);
    }

    /**
     * Returns a blocking list of service names by collecting results from the
     * underlying {@link ReactiveDiscoveryClient}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * List<String> services = adapter.getServices();
     * }</pre>
     *
     * @return the list of service names from the reactive client
     */
    @Override
    public List<String> getServices() {
        Flux<String> flux = this.reactiveDiscoveryClient.getServices();
        return toList(flux);
    }

    /**
     * Delegates to the underlying {@link ReactiveDiscoveryClient#probe()} method to
     * verify the discovery client is operational.
     *
     * <p>Example Usage:
     * <pre>{@code
     * adapter.probe();
     * }</pre>
     */
    @Override
    public void probe() {
        this.reactiveDiscoveryClient.probe();
    }

    /**
     * Returns the order value from the underlying {@link ReactiveDiscoveryClient}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * int order = adapter.getOrder();
     * }</pre>
     *
     * @return the order value of the underlying reactive discovery client
     */
    @Override
    public int getOrder() {
        return this.reactiveDiscoveryClient.getOrder();
    }

    static <T> List<T> toList(Flux<T> flux) {
        Mono<List<T>> mono = flux.collectList();
        if (isInNonBlockingThread()) {
            return execute(() -> mono.toFuture().get());
        }
        return mono.block();
    }
}