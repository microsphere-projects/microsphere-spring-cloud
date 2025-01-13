package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.spring.cloud.openfeign.autorefresh.AutoRefreshCapability;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.openfeign.FeignClientSpecification;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class FeignClientSpecificationPostProcessor implements BeanPostProcessor {


    private static final Class<?> AUTO_REFRESH_CAPABILITY = AutoRefreshCapability.class;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof FeignClientSpecification && beanName.startsWith("default")) {
            injectAutoRefreshCapability((FeignClientSpecification) bean);
        }
        return bean;
    }

    private void injectAutoRefreshCapability(FeignClientSpecification defaultSpecification) {
        Class<?>[] originConfigurationClasses = defaultSpecification.getConfiguration();
        int length = originConfigurationClasses.length;
        Class<?>[] replacedConfigurationClasses = new Class<?>[originConfigurationClasses.length + 1];
        //copy origin and inject AutoRefreshCapability
        System.arraycopy(originConfigurationClasses, 0, replacedConfigurationClasses, 0, length);
        replacedConfigurationClasses[length] = AUTO_REFRESH_CAPABILITY;
        defaultSpecification.setConfiguration(replacedConfigurationClasses);
    }
}
