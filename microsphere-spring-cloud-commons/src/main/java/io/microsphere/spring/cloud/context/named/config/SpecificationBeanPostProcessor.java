package io.microsphere.spring.cloud.context.named.config;

import io.microsphere.spring.beans.factory.config.GenericBeanPostProcessorAdapter;
import io.microsphere.spring.cloud.context.named.SpecificationCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.context.named.NamedContextFactory.Specification;

import java.util.List;

import static io.microsphere.spring.beans.BeanUtils.getSortedBeans;

/**
 * {@link BeanPostProcessor} for {@link Specification}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SpecificationCustomizer
 * @since 0.0.1
 */
public class SpecificationBeanPostProcessor extends GenericBeanPostProcessorAdapter<Specification> implements BeanFactoryAware {

    private List<SpecificationCustomizer> customizers;

    @Override
    protected void processAfterInitialization(Specification bean, String beanName) throws BeansException {
        customizers.forEach(customizer -> customizer.customize(bean, beanName));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.customizers = getSortedBeans(beanFactory, SpecificationCustomizer.class);
    }
}