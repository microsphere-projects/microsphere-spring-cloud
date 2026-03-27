package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.spring.cloud.openfeign.autoconfigure.EnableFeignAutoRefresh.Marker;
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

import static io.microsphere.spring.cloud.openfeign.components.NoOpRequestInterceptor.INSTANCE;

/**
 * The Auto-Configuration class for {@link EnableFeignAutoRefresh}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EnableFeignAutoRefresh
 * @since 0.0.1
 */
@ConditionalOnBean(Marker.class)
public class FeignClientAutoRefreshAutoConfiguration {

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

    /**
     * Handles the {@link ApplicationReadyEvent} to register the
     * {@link FeignClientConfigurationChangedListener} after the application is fully initialized.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Invoked automatically by the Spring event system on application ready
     * onApplicationReadyEvent(applicationReadyEvent);
     * }</pre>
     *
     * @param event the {@link ApplicationReadyEvent} fired when the application is ready
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
        /**
         * Make sure the FeignClientConfigurationChangedListener is registered after the ConfigurationPropertiesRebinder
         */
        registerFeignClientConfigurationChangedListener(event);
    }

    /**
     * Creates the {@link FeignComponentRegistry} bean that tracks decorated Feign components
     * and supports auto-refresh when configuration properties change.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Automatically registered as a Spring bean
     * FeignComponentRegistry registry = feignClientRegistry(clientProperties, beanFactory);
     * }</pre>
     *
     * @param clientProperties the {@link FeignClientProperties} providing the default config name
     * @param beanFactory      the {@link BeanFactory} used for component instantiation
     * @return a new {@link FeignComponentRegistry} instance
     */
    @Bean
    public FeignComponentRegistry feignClientRegistry(FeignClientProperties clientProperties, BeanFactory beanFactory) {
        return new FeignComponentRegistry(clientProperties.getDefaultConfig(), beanFactory);
    }

    /**
     * Creates the {@link FeignClientSpecificationPostProcessor} bean that injects
     * the {@link io.microsphere.spring.cloud.openfeign.autorefresh.AutoRefreshCapability}
     * into default Feign client specifications.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Automatically registered as a Spring bean
     * FeignClientSpecificationPostProcessor processor = feignClientSpecificationPostProcessor();
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
