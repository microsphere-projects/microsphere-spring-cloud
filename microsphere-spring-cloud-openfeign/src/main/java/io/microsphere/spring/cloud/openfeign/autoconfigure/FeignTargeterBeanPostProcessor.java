package io.microsphere.spring.cloud.openfeign.autoconfigure;

import feign.Feign;
import io.microsphere.spring.cloud.openfeign.DelegatingTargeter;
import io.microsphere.spring.cloud.openfeign.RefreshableBuilder;
import io.microsphere.spring.cloud.openfeign.autorefresh.FeignComponentRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.Targeter;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class FeignTargeterBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private final FeignComponentRegistry registry;
    private final FeignContext feignContext;

    private BeanFactory beanFactory;

    public FeignTargeterBeanPostProcessor(FeignComponentRegistry registry, FeignContext feignContext) {
        this.registry = registry;
        this.feignContext = feignContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Targeter) {
            return new DelegatingTargeter(beanFactory, registry, (Targeter) bean);
        }

        return bean;
    }
}
