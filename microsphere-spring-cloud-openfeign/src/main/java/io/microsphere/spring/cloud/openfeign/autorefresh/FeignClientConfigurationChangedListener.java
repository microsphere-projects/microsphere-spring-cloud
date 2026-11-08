package io.microsphere.spring.cloud.openfeign.autorefresh;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.Set;

import static io.microsphere.collection.MapUtils.newHashMap;
import static io.microsphere.collection.SetUtils.newLinkedHashSet;

/**
 * {@link ApplicationListener} of {@link EnvironmentChangeEvent} to refresh the Feign components
 * whose configurations (spring.cloud.openfeign.client.config.*) have been changed.
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @since 1.0.0
 */
public class FeignClientConfigurationChangedListener implements ApplicationListener<EnvironmentChangeEvent> {

    private static final String PREFIX = "spring.cloud.openfeign.client.config.";

    private final FeignComponentRegistry registry;

    /**
     * Constructs a listener that refreshes Feign components when the environment changes.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * FeignComponentRegistry registry = ...;
     * FeignClientConfigurationChangedListener listener =
     *     new FeignClientConfigurationChangedListener(registry);
     * }</pre>
     *
     * @param registry the {@link FeignComponentRegistry} used to refresh affected Feign components
     */
    public FeignClientConfigurationChangedListener(FeignComponentRegistry registry) {
        this.registry = registry;
    }

    /**
     * Handles an {@link EnvironmentChangeEvent} by resolving which Feign clients are affected
     * and triggering a refresh on the corresponding components in the registry.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * // Invoked automatically by the Spring event system
     * listener.onApplicationEvent(environmentChangeEvent);
     * }</pre>
     *
     * @param event the {@link EnvironmentChangeEvent} containing the changed property keys
     */
    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Map<String, Set<String>> effectiveClients = resolveChangedClient(event);
        if (!effectiveClients.isEmpty()) {
            effectiveClients.forEach(registry::refresh);
        }
    }

    /**
     * Resolves which Feign client names and their changed configuration keys are affected
     * by the given {@link EnvironmentChangeEvent}.
     *
     * <p>The changed keys are expected to be in the form of
     * {@code spring.cloud.openfeign.client.config.<clientName>.<subKey>}. The client name is
     * resolved by matching the longest registered client name prefix, so that client names
     * containing the '.' character are handled correctly. Keys without a sub-configuration
     * (e.g. {@code spring.cloud.openfeign.client.config.<clientName>}) or keys of unregistered
     * clients are ignored.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * Map<String, Set<String>> changed = listener.resolveChangedClient(event);
     * // e.g. {"my-client" -> {"my-client.retryer", "my-client.decoder"}}
     * }</pre>
     *
     * @param event the {@link EnvironmentChangeEvent} containing the changed property keys
     * @return a map of client names to their changed configuration keys (prefixed with the client name)
     */
    protected Map<String, Set<String>> resolveChangedClient(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        if (keys.isEmpty()) {
            return newHashMap(0);
        }
        Set<String> registeredClientNames = this.registry.getClientNames();
        Map<String, Set<String>> effectiveClients = newHashMap(registeredClientNames.size());
        for (String key : keys) {
            if (!key.startsWith(PREFIX)) {
                continue;
            }
            String changedConfig = key.substring(PREFIX.length());
            String clientName = matchClientName(changedConfig, registeredClientNames);
            if (clientName == null) {
                // Not a registered client, or the key carries no sub-configuration
                continue;
            }
            effectiveClients.computeIfAbsent(clientName, name -> newLinkedHashSet()).add(changedConfig);
        }
        return effectiveClients;
    }

    /**
     * Matches the longest registered client name that the given changed configuration
     * starts with, supporting client names containing the '.' character.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * String clientName = FeignClientConfigurationChangedListener.matchClientName(
     *     "a.b.retryer", Set.of("a", "a.b")); // returns "a.b"
     * }</pre>
     *
     * @param changedConfig         the changed configuration key without the property prefix,
     *                              e.g. {@code "my-client.retryer"}
     * @param registeredClientNames the registered Feign client names
     * @return the matched client name, or {@code null} if no registered client name matches
     */
    protected static String matchClientName(String changedConfig, Set<String> registeredClientNames) {
        String matchedClientName = null;
        for (String clientName : registeredClientNames) {
            if (clientName != null && changedConfig.startsWith(clientName + ".")
                    && (matchedClientName == null || clientName.length() > matchedClientName.length())) {
                matchedClientName = clientName;
            }
        }
        return matchedClientName;
    }
}
