package io.microsphere.spring.cloud.openfeign.autoconfigure;

import feign.Feign;
import io.microsphere.spring.cloud.openfeign.RefreshableBuilder;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignClientConfigurationChangedListener;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
class FeignClientAutoRefreshConfiguration {

    @Bean
    public FeignTargeterBeanPostProcessor feignTargeterBeanPostProcessor(FeignComponentRegistry registry, FeignContext feignContext) {
        return new FeignTargeterBeanPostProcessor(registry, feignContext);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Feign.Builder refreshableBuilder(BeanFactory beanFactory, FeignComponentRegistry registry, FeignContext feignContext) {
        return new RefreshableBuilder(beanFactory, registry, new Feign.Builder(), feignContext);
    }

    @Bean
    public FeignClientConfigurationChangedListener feignClientConfigurationChangedListener(FeignComponentRegistry registry) {
        return new FeignClientConfigurationChangedListener(registry);
    }

    @Bean
    public FeignComponentRegistry feignClientRegistry() {
        return new FeignComponentRegistry();
    }

}
