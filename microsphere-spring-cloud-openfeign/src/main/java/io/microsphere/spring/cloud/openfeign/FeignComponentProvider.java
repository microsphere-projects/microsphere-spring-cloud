package io.microsphere.spring.cloud.openfeign;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.cloud.openfeign.FeignClientSpecification;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class FeignComponentProvider implements BeanFactoryAware {

    private final NamedContextFactory<FeignClientSpecification> contextFactory;

    private BeanFactory mainBeanFactory;

    public FeignComponentProvider(NamedContextFactory<FeignClientSpecification> contextFactory) {
        this.contextFactory = contextFactory;
    }

    public <T> T getInstance(String contextName, Class<T> type) {
        T component = this.contextFactory.getInstance(contextName, type);
        if (component == null && mainBeanFactory instanceof AutowireCapableBeanFactory) {
            //create new
            return ((AutowireCapableBeanFactory)this.mainBeanFactory).createBean(type);
        }
        return component;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.mainBeanFactory = beanFactory;
    }
}
