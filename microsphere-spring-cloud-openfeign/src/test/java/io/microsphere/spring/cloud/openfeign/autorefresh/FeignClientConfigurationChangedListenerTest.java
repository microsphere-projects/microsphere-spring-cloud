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

package io.microsphere.spring.cloud.openfeign.autorefresh;

import feign.RetryableException;
import feign.Retryer;
import io.microsphere.spring.cloud.openfeign.components.Refreshable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link FeignClientConfigurationChangedListener} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see FeignClientConfigurationChangedListener
 * @since 1.0.0
 */
class FeignClientConfigurationChangedListenerTest {

    private static final String PREFIX = "spring.cloud.openfeign.client.config.";

    private FeignComponentRegistry registry;

    private FeignClientConfigurationChangedListener listener;

    @BeforeEach
    void setUp() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.refresh();
        this.registry = new FeignComponentRegistry("default", context);
        this.listener = new FeignClientConfigurationChangedListener(this.registry);
    }

    @Test
    void testOnNoChangedKeys() {
        EnvironmentChangeEvent event = new EnvironmentChangeEvent(Set.of());
        assertTrue(this.listener.resolveChangedClient(event).isEmpty());
    }

    @Test
    void testKeyWithoutSubConfigIsIgnored() {
        // Previously threw StringIndexOutOfBoundsException
        this.registry.register("test-client", new TestRetryer());
        EnvironmentChangeEvent event = new EnvironmentChangeEvent(Set.of(PREFIX + "test-client"));
        assertTrue(this.listener.resolveChangedClient(event).isEmpty());
    }

    @Test
    void testClientNameContainingDot() {
        // Both "a" and "a.b" are registered clients; the longest prefix must win
        this.registry.register("a", new TestRetryer());
        this.registry.register("a.b", new TestRetryer());
        EnvironmentChangeEvent event = new EnvironmentChangeEvent(Set.of(PREFIX + "a.b.retryer"));
        Map<String, Set<String>> effectiveClients = this.listener.resolveChangedClient(event);
        assertEquals(Set.of("a.b"), effectiveClients.keySet());
        assertEquals(Set.of("a.b.retryer"), effectiveClients.get("a.b"));
    }

    @Test
    void testUnregisteredClientIsIgnored() {
        EnvironmentChangeEvent event = new EnvironmentChangeEvent(Set.of(PREFIX + "unknown-client.retryer"));
        assertTrue(this.listener.resolveChangedClient(event).isEmpty());
    }

    @Test
    void testUnrelatedKeysAreIgnored() {
        this.registry.register("test-client", new TestRetryer());
        EnvironmentChangeEvent event = new EnvironmentChangeEvent(Set.of("server.port", PREFIX + "test-client.retryer"));
        Map<String, Set<String>> effectiveClients = this.listener.resolveChangedClient(event);
        assertEquals(Set.of("test-client"), effectiveClients.keySet());
        assertEquals(Set.of("test-client.retryer"), effectiveClients.get("test-client"));
    }

    @Test
    void testDefaultClientConfig() {
        this.registry.register("test-client", new TestRetryer());
        EnvironmentChangeEvent event = new EnvironmentChangeEvent(Set.of(PREFIX + "default.retryer"));
        Map<String, Set<String>> effectiveClients = this.listener.resolveChangedClient(event);
        assertEquals(Set.of("default"), effectiveClients.keySet());
    }

    @Test
    void testOnApplicationEvent() {
        TestRetryer testRetryer = new TestRetryer();
        this.registry.register("test-client", testRetryer);
        EnvironmentChangeEvent event = new EnvironmentChangeEvent(Set.of(PREFIX + "test-client.retryer"));
        this.listener.onApplicationEvent(event);
        assertEquals(1, testRetryer.refreshedCount);
    }

    static class TestRetryer implements Retryer, Refreshable {

        int refreshedCount = 0;

        @Override
        public void refresh() {
            this.refreshedCount++;
        }

        @Override
        public void continueOrPropagate(RetryableException e) {
            throw e;
        }

        @Override
        public Retryer clone() {
            return this;
        }
    }
}
