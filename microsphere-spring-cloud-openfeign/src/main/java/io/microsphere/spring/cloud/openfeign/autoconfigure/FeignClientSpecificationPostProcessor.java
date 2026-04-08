package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.logging.Logger;
import io.microsphere.spring.beans.factory.config.GenericBeanPostProcessorAdapter;
import io.microsphere.spring.cloud.openfeign.autorefresh.AutoRefreshCapability;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.openfeign.FeignClientSpecification;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.util.ArrayUtils.arrayToString;
import static io.microsphere.util.ArrayUtils.combine;

/**
 * {@link BeanPostProcessor} for {@link FeignClientSpecification}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see org.springframework.cloud.openfeign.FeignClientSpecification
 * @since 0.0.1
 */
public class FeignClientSpecificationPostProcessor extends GenericBeanPostProcessorAdapter<FeignClientSpecification> {

    private static final Logger logger = getLogger(FeignClientSpecificationPostProcessor.class);

    static final Class<?> AUTO_REFRESH_CAPABILITY_CLASS = AutoRefreshCapability.class;

    static final Class<?> FEIGN_CLIENT_SPECIFICATION_CLASS = FeignClientSpecification.class;

    /**
     * Injects the {@link AutoRefreshCapability} into default {@link FeignClientSpecification}
     * beans after initialization.
     *
     * @param bean     {@link FeignClientSpecification}
     * @param beanName the name of the bean in the Spring context
     * @return the (possibly modified) bean instance
     * @throws BeansException if post-processing fails
     */
    @Override
    protected void processAfterInitialization(FeignClientSpecification bean, String beanName) throws BeansException {
        if (beanName.startsWith("default.")) {
            injectAutoRefreshCapability(bean);
        }
    }

    void injectAutoRefreshCapability(FeignClientSpecification specification) {
        Class<?>[] originConfigurationClasses = specification.getConfiguration();
        Class<?>[] newConfigurationClasses = combine(AUTO_REFRESH_CAPABILITY_CLASS, originConfigurationClasses);
        specification.setConfiguration(newConfigurationClasses);
        if (logger.isTraceEnabled()) {
            logger.trace("The Configuration classes: before - {} , after - {}", arrayToString(originConfigurationClasses),
                    arrayToString(newConfigurationClasses));
        }
    }
}