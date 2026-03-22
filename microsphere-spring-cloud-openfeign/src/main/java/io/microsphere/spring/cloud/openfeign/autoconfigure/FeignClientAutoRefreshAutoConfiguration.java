package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.spring.cloud.openfeign.autorefresh.FeignClientConfigurationChangedListener;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry;
import io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
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

    /**
     * Creates a {@link FeignBuilderCustomizer} that adds a {@link NoOpRequestInterceptor}
     * as the default request interceptor for all Feign clients. This ensures that the
     * auto-refresh mechanism has an interceptor entry point to work with.
     *
     * <p>Example Usage:
     * <pre>{@code
     * FeignBuilderCustomizer customizer = addDefaultRequestInterceptorCustomizer();
     * // customizer is applied automatically by Spring to each Feign builder
     * }</pre>
     *
     * @return a {@link FeignBuilderCustomizer} that registers the no-op request interceptor
     */
    @Bean
    public FeignBuilderCustomizer addDefaultRequestInterceptorCustomizer() {
        return builder -> {
            builder.requestInterceptor(NoOpRequestInterceptor.INSTANCE);
        };
    }

    /**
     * Handles the {@link ApplicationReadyEvent} to register a
     * {@link FeignClientConfigurationChangedListener} after the application context
     * is fully initialized. This ensures that the listener is registered after the
     * {@code ConfigurationPropertiesRebinder}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Automatically invoked by Spring when the application is ready:
     * // context.publishEvent(new ApplicationReadyEvent(...));
     * }</pre>
     *
     * @param event the {@link ApplicationReadyEvent} indicating the application is ready
     */
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

    /**
     * Creates a {@link FeignClientSpecificationPostProcessor} bean that injects the
     * {@link io.microsphere.spring.cloud.openfeign.autorefresh.AutoRefreshCapability}
     * into default Feign client specifications.
     *
     * <p>Example Usage:
     * <pre>{@code
     * FeignClientSpecificationPostProcessor processor = feignClientSpecificationPostProcessor();
     * // processor is registered as a BeanPostProcessor by Spring automatically
     * }</pre>
     *
     * @return a new {@link FeignClientSpecificationPostProcessor} instance
     */
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
