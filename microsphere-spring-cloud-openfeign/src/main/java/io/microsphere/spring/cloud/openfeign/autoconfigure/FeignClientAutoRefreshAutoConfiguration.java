package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.spring.cloud.openfeign.autorefresh.EnableFeignAutoRefresh;
import io.microsphere.spring.cloud.openfeign.autorefresh.EnableFeignAutoRefresh.Marker;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignClientConfigurationChangedListener;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
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
@ConditionalOnBean(Marker.class)
public class FeignClientAutoRefreshAutoConfiguration {

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

    private void registerFeignClientConfigurationChangedListener(ApplicationReadyEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        FeignComponentRegistry feignComponentRegistry = context.getBean(FeignComponentRegistry.class);
        context.addApplicationListener(new FeignClientConfigurationChangedListener(feignComponentRegistry));
    }
}