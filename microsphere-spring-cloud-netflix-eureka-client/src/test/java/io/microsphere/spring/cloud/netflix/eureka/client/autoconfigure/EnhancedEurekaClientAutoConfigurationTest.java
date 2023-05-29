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
package io.microsphere.spring.cloud.netflix.eureka.client.autoconfigure;

import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.discovery.EurekaEventListener;
import com.netflix.discovery.PreRegistrationHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link EnhancedEurekaClientAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        EnhancedEurekaClientAutoConfigurationTest.class,
        EnhancedEurekaClientAutoConfigurationTest.Config.class
})
@TestPropertySource(
        properties = {
                "spring.application.name=test-eureka",
                "server.port=12345",
                "eureka.client.serviceUrl.defaultZone=http://127.0.0.1:12345/eureka"
        }
)
@EnableAutoConfiguration
@EnableEurekaServer
public class EnhancedEurekaClientAutoConfigurationTest {

    private static final AtomicBoolean preRegistered = new AtomicBoolean(false);

    static class Config {
        @Bean
        public PreRegistrationHandler preRegistrationHandler() {
            return () -> {
                preRegistered.set(true);
            };
        }

        @Bean
        public EurekaEventListener eurekaEventListener() {
            return event -> {
                System.out.println(event);
            };
        }

        @Bean
        public HealthCheckHandler healthCheckHandler() {
            return currentStatus -> currentStatus;
        }
    }

    @Test
    public void test() throws Throwable {
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertTrue(preRegistered.get());
    }
}
