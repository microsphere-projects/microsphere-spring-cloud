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

package io.microsphere.spring.cloud.client.discovery.autoconfigure;

import io.microsphere.spring.boot.test.AutoConfigurationTest;
import io.microsphere.spring.cloud.client.discovery.UnionDiscoveryClient;
import io.microsphere.spring.cloud.client.discovery.autoconfigure.DiscoveryClientAutoConfiguration.UnionConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.Set;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

/**
 * {@link DiscoveryClientAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DiscoveryClientAutoConfiguration
 * @see AutoConfigurationTest
 * @since 1.0.0
 */
@SpringBootTest(classes = {
        DiscoveryClientAutoConfigurationTest.class,
}, webEnvironment = NONE,
        properties = {
                "microsphere.spring.cloud.client.discovery.mode=union"
        })
public class DiscoveryClientAutoConfigurationTest extends AutoConfigurationTest<DiscoveryClientAutoConfiguration> {

    @Override
    protected void configureAutoConfiguredClasses(Set<Class<?>> autoConfiguredClasses) {
        autoConfiguredClasses.add(UnionConfiguration.class);
        autoConfiguredClasses.add(UnionDiscoveryClient.class);
    }

    @Override
    protected void configureGlobalDisabledPropertyValues(Set<String> globalDisabledPropertyValues) {
        globalDisabledPropertyValues.add("spring.cloud.discovery.enabled=false");
        globalDisabledPropertyValues.add("spring.cloud.discovery.blocking.enabled=false");
    }

    @Override
    protected void configureGlobalMissingClasses(Set<Class<?>> globalMissingClasses) {
        globalMissingClasses.add(DiscoveryClient.class);
    }
}