package io.microsphere.spring.cloud.openfeign;

import io.microsphere.spring.cloud.openfeign.autoconfigure.EnableFeignAutoRefresh;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
@SpringBootTest(classes = {
        BaseTest.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ComponentScan(basePackages = "io.microsphere.spring.cloud.openfeign")
@TestPropertySource(
        properties = {
            "feign.client.config.default.encoder=io.microsphere.spring.cloud.openfeign.encoder.AEncoder",
            "feign.client.config.default.request-interceptors[0]=io.microsphere.spring.cloud.openfeign.requestInterceptor.ARequestInterceptor"
        }
)
@EnableFeignClients(clients = BaseClient.class)
@EnableFeignAutoRefresh
public class BaseTest {
}
