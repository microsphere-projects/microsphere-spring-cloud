package io.github.microsphere.spring.cloud.context.autoconfigure;

import io.microsphere.spring.beans.factory.annotation.RefreshableConfigurationBeanPredicate;
import io.microsphere.spring.beans.factory.annotation.RefreshableConfigurationBeans;
import io.github.microsphere.spring.cloud.context.ConfigurationBeanRefreshEndpoint;
import io.github.microsphere.spring.cloud.context.ConfigurationBeanRefresher;
import io.github.microsphere.spring.cloud.context.RefreshScopeConfigurationBeanPredicate;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RefreshableConfigurationBeans.class)
@ConditionalOnBean(name = RefreshableConfigurationBeans.BEAN_NAME)
public class ConfigurationBeanRefresherAutoConfiguration {

    @Bean
    @ConditionalOnClass(RefreshScope.class)
    public RefreshableConfigurationBeanPredicate refreshScopePredicate() {
        return new RefreshScopeConfigurationBeanPredicate();
    }

    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    public ConfigurationBeanRefresher configurationBeanRefresher(RefreshableConfigurationBeans configurationBeans) {
        return new ConfigurationBeanRefresher(configurationBeans);
    }

    @Bean
    @ConditionalOnBean(ConfigurationBeanRefresher.class)
    @ConditionalOnAvailableEndpoint(endpoint = ConfigurationBeanRefreshEndpoint.class)
    @ConditionalOnMissingBean
    public ConfigurationBeanRefreshEndpoint configurationBeanRefreshEndpoint(ConfigurationBeanRefresher refresher) {
        return new ConfigurationBeanRefreshEndpoint(refresher);
    }
}
