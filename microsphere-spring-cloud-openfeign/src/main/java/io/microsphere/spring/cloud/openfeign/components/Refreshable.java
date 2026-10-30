package io.microsphere.spring.cloud.openfeign.components;

/**
 * A functional interface for components that support refresh/reload operations.
 * <p>
 * This interface represents objects that can refresh their state or reload their configuration,
 * commonly used in Feign client components that need to adapt to changing configurations.
 *
 * <h3>Example Usage</h3>
 * <pre>{@code
 * // Implement Refreshable for a component that needs refresh capability
 * public class MyFeignClient implements Refreshable {
 *     private Object currentConfig;
 *
 *     @Override
 *     public void refresh() {
 *         // Reload configuration from external source
 *         this.currentConfig = loadNewConfiguration();
 *     }
 * }
 *
 * // Use refresh on any Refreshable component
 * MyFeignClient client = new MyFeignClient();
 * client.refresh(); // Trigger a reload
 * }</pre>
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see DecoratedFeignComponent
 * @since 1.0.0
 */
@FunctionalInterface
public interface Refreshable {

    /**
     * Refreshes the component, typically reloading configuration or resetting internal state.
     * <p>
     * This method is invoked when the component needs to be refreshed, such as when
     * configuration changes are detected or when explicitly triggered by a refresh operation.
     *
     * <h3>Example Usage</h3>
     * <pre>{@code
     * Refreshable myComponent = new MyRefreshableComponent();
     * myComponent.refresh(); // Trigger refresh operation
     * }</pre>
     */
    void refresh();
}