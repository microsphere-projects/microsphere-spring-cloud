package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.spring.cloud.openfeign.autorefresh.FeignClientConfigurationChangedListener;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry;
import io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * The Auto-Configuration class for {@link EnableFeignAutoRefresh}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EnableFeignAutoRefresh
 * @since 0.0.1
 */
@ConditionalOnBean(EnableFeignAutoRefresh.Marker.class)
public class FeignClientAutoRefreshAutoConfiguration {

    @Bean
    public FeignBuilderCustomizer addDefaultRequestInterceptorCustomizer() {
        return builder -> {
            builder.requestInterceptor(NoOpRequestInterceptor.INSTANCE);
        };
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
        /**
         * Make sure the FeignClientConfigurationChangedListener is registered after the ConfigurationPropertiesRebinder
         */
        registerFeignClientConfigurationChangedListener(event);
    }

    @Bean
    public FeignComponentRegistry feignClientRegistry(FeignClientProperties clientProperties, BeanFactory beanFactory) {
        return new FeignComponentRegistry(clientProperties.getDefaultConfig(), beanFactory);
    }

    @Bean
    public FeignClientSpecificationPostProcessor feignClientSpecificationPostProcessor() {
        return new FeignClientSpecificationPostProcessor();
    }

    private void registerFeignClientConfigurationChangedListener(ApplicationReadyEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        FeignComponentRegistry feignComponentRegistry = context.getBean(FeignComponentRegistry.class);
        context.addApplicationListener(new FeignClientConfigurationChangedListener(feignComponentRegistry));
    }

}
