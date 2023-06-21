package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.spring.cloud.openfeign.autorefresh.FeignClientConfigurationChangedListener;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
class FeignClientAutoRefreshConfiguration {

    @Bean
    public FeignClientConfigurationChangedListener feignClientConfigurationChangedListener(FeignComponentRegistry registry) {
        return new FeignClientConfigurationChangedListener(registry);
    }

    @Bean
    public FeignComponentRegistry feignClientRegistry(BeanFactory beanFactory) {
        return new FeignComponentRegistry("default", beanFactory);
    }

}
