package io.microsphere.spring.cloud.openfeign.autorefresh;

import io.microsphere.spring.cloud.openfeign.components.Refreshable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 0.0.1
 */
public class FeignComponentRegistry {

    private final Map<String, List<Refreshable>> refreshableComponents = new ConcurrentHashMap<>(32);

    private final String DEFAULT_CLIENT_NAME;

    public FeignComponentRegistry(String defaultClientName) {
        this.DEFAULT_CLIENT_NAME = defaultClientName;
    }

    public void register(String clientName, List<Refreshable> components) {
        List<Refreshable> componentList = this.refreshableComponents.computeIfAbsent(clientName, name -> new ArrayList<>());
        componentList.addAll(componentList);
    }

    public void register(String clientName, Refreshable component) {
        List<Refreshable> componentList = this.refreshableComponents.computeIfAbsent(clientName, name -> new ArrayList<>());
        componentList.add(component);
    }


    public void refresh(String clientName) {
        if (DEFAULT_CLIENT_NAME.equals(clientName)) {
            //default configs changed, need refresh all
            refreshableComponents.values().stream()
                    .flatMap(List::stream)
                    .forEach(Refreshable::refresh);
            return;
        }
        List<Refreshable> components = this.refreshableComponents.get(clientName);
        if (CollectionUtils.isEmpty(components))
            return;

        components.forEach(Refreshable::refresh);
    }

}
