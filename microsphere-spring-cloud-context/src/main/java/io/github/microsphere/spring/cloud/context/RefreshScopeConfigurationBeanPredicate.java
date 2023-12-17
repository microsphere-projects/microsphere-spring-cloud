package io.github.microsphere.spring.cloud.context;

import io.microsphere.spring.beans.factory.annotation.RefreshableConfigurationBeanPredicate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

/**
 * Prevent configuration bean with {@link org.springframework.cloud.context.scope.refresh.RefreshScope} register to {@link org.springframework.cloud.context.properties.ConfigurationPropertiesBeans}
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public class RefreshScopeConfigurationBeanPredicate implements RefreshableConfigurationBeanPredicate, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;
    private volatile String refreshScope;
    private volatile boolean refreshScopeChecked;

    @Override
    public boolean support(String beanName, Object instance, BeanDefinition beanDefinition) {
        if (this.refreshScope == null && !refreshScopeChecked) {
            refreshScopeChecked = true;
            for (String scope : this.beanFactory.getRegisteredScopeNames()) {
                if (this.beanFactory.getRegisteredScope(
                        scope) instanceof org.springframework.cloud.context.scope.refresh.RefreshScope) {
                    this.refreshScope = scope;
                    break;
                }
            }
        }
        if (this.refreshScope == null) {
            return true;
        }
        return !this.refreshScope.equals(beanDefinition.getScope());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory);
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
