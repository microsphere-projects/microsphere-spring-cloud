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

import com.netflix.discovery.EurekaClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link MultipleEurekaClientAutoConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        MultipleEurekaClientAutoConfigurationTest.class
})
@TestPropertySource(
        properties = {
                "spring.application.name=test-eureka",
                "server.port=12345",
                "eureka.client.serviceUrl.defaultZone=http://127.0.0.1:12345/eureka,http://127.0.0.1:12345/eureka",
                "microsphere.spring.cloud.eureka.client.multiple=true"
        }
)
@EnableAutoConfiguration
@EnableEurekaServer
public class MultipleEurekaClientAutoConfigurationTest {

    @Autowired
    private MultipleEurekaClientAutoConfiguration config;

    @Test
    public void test() throws Throwable {
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        List<EurekaClient> eurekaClients = config.getEurekaClients();
        assertEquals(2, eurekaClients.size());
    }
}
