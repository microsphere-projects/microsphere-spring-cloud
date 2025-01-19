package io.microsphere.spring.cloud.openfeign.autorefresh;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class FeignClientConfigurationChangedListener implements ApplicationListener<EnvironmentChangeEvent> {

    private final FeignComponentRegistry registry;

    public FeignClientConfigurationChangedListener(FeignComponentRegistry registry) {
        this.registry = registry;
    }

    private final String PREFIX = "feign.client.config.";

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Map<String, Set<String>> effectiveClients = resolveChangedClient(event);
        if (!effectiveClients.isEmpty()) {
            effectiveClients.forEach(registry::refresh);
        }
    }

    protected Map<String, Set<String>> resolveChangedClient(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        return keys.stream()
                .filter(str -> str.startsWith(PREFIX))
                .map(str -> str.replace(PREFIX, ""))
                .collect(Collectors.groupingBy(str -> {
                    int index = str.indexOf(".");
                    return str.substring(0, index);
                }, Collectors.toSet()));

    }

}
