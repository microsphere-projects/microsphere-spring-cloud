package io.microsphere.spring.cloud.openfeign.autoconfigure;

import feign.Feign;
import io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor.INSTANCE;

/**
 * The Auto-Configuration class for Spring Cloud OpenFeign
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Feign.class)
@AutoConfigureAfter(name = "org.springframework.cloud.openfeign.FeignAutoConfiguration")
public class FeignAutoConfiguration {

    /**
     * Creates a {@link FeignBuilderCustomizer} that adds the {@link NoOpRequestInterceptor}
     * as a default request interceptor to every Feign client builder.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Automatically registered as a Spring bean; customizes every Feign builder
     * FeignBuilderCustomizer customizer = addDefaultRequestInterceptorCustomizer();
     * }</pre>
     *
     * @return a {@link FeignBuilderCustomizer} that adds the {@link NoOpRequestInterceptor}
     */
    @Bean
    public FeignBuilderCustomizer addDefaultRequestInterceptorCustomizer() {
        return builder -> {
            builder.requestInterceptor(INSTANCE);
        };
    }
}