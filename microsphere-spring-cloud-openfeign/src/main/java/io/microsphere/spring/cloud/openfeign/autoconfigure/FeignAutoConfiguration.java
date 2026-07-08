package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.annotation.ConfigurationProperty;
import io.microsphere.constants.PropertyConstants;
import io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.microsphere.annotation.ConfigurationProperty.APPLICATION_SOURCE;
import static io.microsphere.constants.SymbolConstants.DOT;
import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX;
import static io.microsphere.spring.cloud.openfeign.autoconfigure.FeignAutoConfiguration.ENABLED_PROPERTY_NAME;
import static io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor.INSTANCE;

/**
 * The Auto-Configuration class for Spring Cloud OpenFeign
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = ENABLED_PROPERTY_NAME, matchIfMissing = true)
@ConditionalOnClass(name = {
        "feign.Feign",                                                  // OpenFeign Core API
        "org.springframework.cloud.openfeign.FeignBuilderCustomizer"    // Spring Cloud OpenFeign API
})
@AutoConfigureAfter(name = {
        "org.springframework.cloud.openfeign.FeignAutoConfiguration"
})
public class FeignAutoConfiguration {

    /**
     * The property name for enabling {@link FeignAutoConfiguration} : "microsphere.spring.cloud.openfeign.enabled"
     */
    @ConfigurationProperty(
            type = boolean.class,
            defaultValue = "true",
            source = APPLICATION_SOURCE
    )
    public static final String ENABLED_PROPERTY_NAME = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "openfeign" + DOT + PropertyConstants.ENABLED_PROPERTY_NAME;

    /**
     * Creates a {@link FeignBuilderCustomizer} that adds the {@link NoOpRequestInterceptor}
     * as a default request interceptor to every Feign client builder.
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