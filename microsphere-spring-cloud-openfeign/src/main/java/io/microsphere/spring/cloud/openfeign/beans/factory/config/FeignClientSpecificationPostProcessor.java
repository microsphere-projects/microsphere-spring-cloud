package io.microsphere.spring.cloud.openfeign.beans.factory.config;

import io.microsphere.spring.beans.factory.config.GenericBeanPostProcessorAdapter;
import io.microsphere.spring.cloud.openfeign.FeignClientSpecificationCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.openfeign.FeignClientSpecification;

/**
 * {@link BeanPostProcessor} for {@link FeignClientSpecification}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see org.springframework.cloud.openfeign.FeignClientSpecification
 * @see FeignClientSpecificationCustomizer
 * @since 0.0.1
 */
public class FeignClientSpecificationPostProcessor extends GenericBeanPostProcessorAdapter<FeignClientSpecification> implements BeanFactoryAware {

    private ObjectProvider<FeignClientSpecificationCustomizer> customizerProvider;

    @Override
    protected void processAfterInitialization(FeignClientSpecification bean, String beanName) throws BeansException {
        customizerProvider.forEach(customizer -> customizer.customize(bean, beanName));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.customizerProvider = beanFactory.getBeanProvider(FeignClientSpecificationCustomizer.class);
    }
}