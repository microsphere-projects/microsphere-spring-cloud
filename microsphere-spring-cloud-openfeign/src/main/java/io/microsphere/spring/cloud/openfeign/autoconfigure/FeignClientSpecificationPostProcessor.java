package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.logging.Logger;
import io.microsphere.spring.cloud.openfeign.autorefresh.AutoRefreshCapability;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.openfeign.FeignClientSpecification;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.util.ArrayUtils.arrayToString;
import static io.microsphere.util.ArrayUtils.combine;
import static org.springframework.aop.support.AopUtils.getTargetClass;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see org.springframework.cloud.openfeign.FeignClientSpecification
 * @since 0.0.1
 */
public class FeignClientSpecificationPostProcessor implements BeanPostProcessor {

    private static final Logger logger = getLogger(FeignClientSpecificationPostProcessor.class);

    private static final Class<?> AUTO_REFRESH_CAPABILITY_CLASS = AutoRefreshCapability.class;

    private static final Class<?> FEIGN_CLIENT_SPECIFICATION_CLASS = FeignClientSpecification.class;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanType = getTargetClass(bean);
        if (FEIGN_CLIENT_SPECIFICATION_CLASS.isAssignableFrom(beanType) && beanName.startsWith("default")) {
            injectAutoRefreshCapability((FeignClientSpecification) bean);
        }
        return bean;
    }

    private void injectAutoRefreshCapability(FeignClientSpecification specification) {
        Class<?>[] originConfigurationClasses = specification.getConfiguration();
        Class<?>[] newConfigurationClasses = combine(AUTO_REFRESH_CAPABILITY_CLASS, originConfigurationClasses);
        specification.setConfiguration(newConfigurationClasses);
        logger.trace("The Configuration classes: before - {} , after - {}", arrayToString(originConfigurationClasses), arrayToString(newConfigurationClasses));
    }
}
