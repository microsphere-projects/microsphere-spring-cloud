package io.microsphere.spring.cloud.openfeign.autoconfigure;

import io.microsphere.logging.Logger;
import io.microsphere.spring.cloud.openfeign.autorefresh.AutoRefreshCapability;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.context.named.NamedContextFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.reflect.MethodUtils.findMethod;
import static io.microsphere.util.ArrayUtils.combine;
import static org.springframework.aop.support.AopUtils.getTargetClass;
import static org.springframework.util.ClassUtils.resolveClassName;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see org.springframework.cloud.openfeign.FeignClientSpecification
 * @since 0.0.1
 */
public class FeignClientSpecificationPostProcessor implements BeanPostProcessor {

    private static final Logger logger = getLogger(FeignClientSpecificationPostProcessor.class);

    private static final Class<?> AUTO_REFRESH_CAPABILITY_CLASS = AutoRefreshCapability.class;

    private static final String FEIGN_CLIENT_SPECIFICATION_CLASS_NAME = "org.springframework.cloud.openfeign.FeignClientSpecification";

    private static final Class<?> FEIGN_CLIENT_SPECIFICATION_CLASS = resolveClassName(FEIGN_CLIENT_SPECIFICATION_CLASS_NAME, null);

    private static final Method setConfigurationMethod = findMethod(FEIGN_CLIENT_SPECIFICATION_CLASS, "setConfiguration", Class[].class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanType = getTargetClass(bean);
        if (FEIGN_CLIENT_SPECIFICATION_CLASS.isAssignableFrom(beanType) && beanName.startsWith("default")) {
            injectAutoRefreshCapability((NamedContextFactory.Specification) bean);
        }
        return bean;
    }

    private void injectAutoRefreshCapability(NamedContextFactory.Specification defaultSpecification) {
        if (setConfigurationMethod != null) {
            Class<?>[] originConfigurationClasses = defaultSpecification.getConfiguration();
            Class<?>[] newConfigurationClasses = combine(AUTO_REFRESH_CAPABILITY_CLASS, originConfigurationClasses);
            Object arg = newConfigurationClasses;
            try {
                setConfigurationMethod.setAccessible(true);
                setConfigurationMethod.invoke(defaultSpecification, arg);
            } catch (Throwable e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("FeignClientSpecification#setConfiguration(Class[]) can't be invoked , instance : {} , args : {}",
                            defaultSpecification, Arrays.toString(newConfigurationClasses));
                }
            }
        }
    }
}
