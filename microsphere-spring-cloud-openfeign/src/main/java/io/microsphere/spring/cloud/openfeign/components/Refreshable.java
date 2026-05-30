package io.microsphere.spring.cloud.openfeign.components;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
@FunctionalInterface
public interface Refreshable {

    /**
     * Refresh
     */
    void refresh();
}