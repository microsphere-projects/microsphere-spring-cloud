package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.spring.cloud.openfeign.autorefresh.FeignClientConfigurationChangedListener;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry;
import io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.annotation.Bean;

/**
 * The Auto-Configuration class for {@link EnableFeignAutoRefresh}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EnableFeignAutoRefresh
 * @since 0.0.1
 */
@ConditionalOnBean(EnableFeignAutoRefresh.Marker.class)
@AutoConfigureAfter(ConfigurationPropertiesRebinderAutoConfiguration.class)
public class FeignClientAutoRefreshAutoConfiguration {

    @Bean
    public FeignBuilderCustomizer addDefaultRequestInterceptorCustomizer() {
        return builder -> {
            builder.requestInterceptor(NoOpRequestInterceptor.INSTANCE);
        };
    }

    @Bean
    public FeignClientConfigurationChangedListener feignClientConfigurationChangedListener(FeignComponentRegistry registry) {
        return new FeignClientConfigurationChangedListener(registry);
    }

    @Bean
    public FeignComponentRegistry feignClientRegistry(FeignClientProperties clientProperties, BeanFactory beanFactory) {
        return new FeignComponentRegistry(clientProperties.getDefaultConfig(), beanFactory);
    }

    @Bean
    public FeignClientSpecificationPostProcessor feignClientSpecificationPostProcessor() {
        return new FeignClientSpecificationPostProcessor();
    }

}
