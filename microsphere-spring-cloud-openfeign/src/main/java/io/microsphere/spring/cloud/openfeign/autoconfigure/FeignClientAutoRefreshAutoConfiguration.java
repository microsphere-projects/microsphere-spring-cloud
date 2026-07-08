package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.spring.cloud.openfeign.autorefresh.AutoRefreshCapabilityCustomizer;
import io.microsphere.spring.cloud.openfeign.autorefresh.EnableFeignAutoRefresh;
import io.microsphere.spring.cloud.openfeign.autorefresh.EnableFeignAutoRefresh.Marker;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignClientConfigurationChangedListener;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry;
import io.microsphere.spring.cloud.openfeign.condition.ConditionalOnOpenFeignAvailable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import static io.microsphere.spring.cloud.openfeign.constants.FeignConstants.FEIGN_AUTO_CONFIGURATION_CLASS_NAME;
import static io.microsphere.spring.cloud.openfeign.constants.FeignConstants.FEIGN_CAPABILITY_CLASS_NAME;
import static io.microsphere.spring.cloud.openfeign.constants.FeignConstants.FEIGN_CLIENT_FACTORY_BEAN_CLASS_NAME;

/**
 * The Auto-Configuration class for {@link EnableFeignAutoRefresh}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EnableFeignAutoRefresh
 * @since 1.0.0
 */
@ConditionalOnOpenFeignAvailable
@ConditionalOnClass(name = {
        FEIGN_CAPABILITY_CLASS_NAME,             // OpenFeign Core API
        FEIGN_CLIENT_FACTORY_BEAN_CLASS_NAME     // Spring Cloud OpenFeign API
})
@ConditionalOnBean(Marker.class)
@AutoConfigureAfter(name = {
        FEIGN_AUTO_CONFIGURATION_CLASS_NAME      // Spring Cloud OpenFeign API
})
public class FeignClientAutoRefreshAutoConfiguration {

    /**
     * Handles the {@link ApplicationReadyEvent} to register the
     * {@link FeignClientConfigurationChangedListener} after the application is fully initialized.
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
     * @param clientProperties the {@link FeignClientProperties} providing the default config name
     * @param beanFactory      the {@link BeanFactory} used for component instantiation
     * @return a new {@link FeignComponentRegistry} instance
     */
    @Bean
    @ConditionalOnMissingBean
    public FeignComponentRegistry feignClientRegistry(FeignClientProperties clientProperties, BeanFactory beanFactory) {
        return new FeignComponentRegistry(clientProperties.getDefaultConfig(), beanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public AutoRefreshCapabilityCustomizer autoRefreshCapabilityCustomizer() {
        return new AutoRefreshCapabilityCustomizer();
    }

    private void registerFeignClientConfigurationChangedListener(ApplicationReadyEvent event) {
        ConfigurableApplicationContext context = event.getApplicationContext();
        FeignComponentRegistry feignComponentRegistry = context.getBean(FeignComponentRegistry.class);
        context.addApplicationListener(new FeignClientConfigurationChangedListener(feignComponentRegistry));
    }
}