package io.microsphere.spring.cloud.context;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0
 */
@Endpoint(id = "configuration-bean-refresh")
public class ConfigurationBeanRefreshEndpoint {

    private final ConfigurationBeanRefresher refresher;

    public ConfigurationBeanRefreshEndpoint(ConfigurationBeanRefresher refresher) {
        this.refresher = refresher;
    }

    @WriteOperation
    public void refreshAll() {
        this.refresher.refreshAll();
    }

    @WriteOperation
    public void refreshByName(String name) {
        this.refresher.refreshByName(name);
    }
}
