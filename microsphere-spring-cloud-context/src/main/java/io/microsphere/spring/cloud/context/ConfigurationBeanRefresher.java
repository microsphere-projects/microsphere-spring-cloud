package io.microsphere.spring.cloud.context;

import io.microsphere.spring.beans.factory.annotation.RefreshableConfigurationBeans;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

import java.util.Collection;
import java.util.Set;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public class ConfigurationBeanRefresher implements ApplicationListener<EnvironmentChangeEvent>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final RefreshableConfigurationBeans configurationBeans;

    public ConfigurationBeanRefresher(RefreshableConfigurationBeans configurationBeans) {
        this.configurationBeans = configurationBeans;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void refreshByName(String beanName) {
        this.configurationBeans.refreshByName(beanName, true);
    }

    public void refreshAll() {
        Set<String> beanNames = this.configurationBeans.managedBeanNames();
        for (String beanName : beanNames) {
            this.configurationBeans.refreshByName(beanName, true);
        }
    }

    protected void refreshByKeys(Collection<String> changedKeys) {
        for (String changedKey : changedKeys)
            this.configurationBeans.refreshByProperty(changedKey, true);
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        if (this.applicationContext.equals(event.getSource())
                // Backwards compatible
                || event.getKeys().equals(event.getSource())) {
            refreshByKeys(event.getKeys());
        }
    }
}
