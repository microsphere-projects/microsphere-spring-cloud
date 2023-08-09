package io.microsphere.spring.cloud.gateway.event;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.Set;

/**
 * {@link EnvironmentChangeEvent} {@link ApplicationListener} propagates {@link RefreshRoutesEvent}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see RefreshRoutesEvent
 * @since 1.0.0
 */
public class PropagatingRefreshRoutesEventApplicationListener implements ApplicationListener<EnvironmentChangeEvent> {

    private final ApplicationContext context;

    public PropagatingRefreshRoutesEventApplicationListener(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        if (containsGatewayPropertyName(keys)) {
            context.publishEvent(new RefreshRoutesEvent(this));
        }
    }

    private boolean containsGatewayPropertyName(Set<String> keys) {
        for (String key : keys) {
            if (isGatewayPropertyName(key)) {
                return true;
            }
        }
        return false;
    }

    private boolean isGatewayPropertyName(String key) {
        return key != null && key.startsWith(GatewayProperties.PREFIX);
    }

}
