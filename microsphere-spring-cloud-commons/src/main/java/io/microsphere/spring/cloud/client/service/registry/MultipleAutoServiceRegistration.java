package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

/**
 * {@link AutoServiceRegistration} for the multiple service registration
 *
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AutoServiceRegistration
 * @see MultipleRegistration
 * @since 1.0.0
 */
public class MultipleAutoServiceRegistration extends AbstractAutoServiceRegistration<MultipleRegistration> {

    private final AutoServiceRegistrationProperties autoServiceRegistrationProperties;

    private final MultipleRegistration multipleRegistration;

    /**
     * Constructs a new {@link MultipleAutoServiceRegistration} with the given registration,
     * service registry, and auto-registration properties.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleAutoServiceRegistration autoReg = new MultipleAutoServiceRegistration(
     *     multipleRegistration, serviceRegistry, properties);
     * }</pre>
     *
     * @param multipleRegistration the {@link MultipleRegistration} to auto-register
     * @param serviceRegistry      the {@link ServiceRegistry} to register with
     * @param properties           the {@link AutoServiceRegistrationProperties} configuration
     */
    public MultipleAutoServiceRegistration(MultipleRegistration multipleRegistration,
                                           ServiceRegistry<MultipleRegistration> serviceRegistry,
                                           AutoServiceRegistrationProperties properties) {
        super(serviceRegistry, properties);
        this.autoServiceRegistrationProperties = properties;
        this.multipleRegistration = multipleRegistration;
    }

    /**
     * Returns the configuration object for this auto-registration. Currently returns {@code null}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * Object config = autoRegistration.getConfiguration();
     * }</pre>
     *
     * @return {@code null}
     */
    @Override
    protected Object getConfiguration() {
        return null;
    }

    /**
     * Returns whether auto service registration is enabled.
     *
     * <p>Example Usage:
     * <pre>{@code
     * boolean enabled = autoRegistration.isEnabled();
     * }</pre>
     *
     * @return {@code true} if auto service registration is enabled
     */
    @Override
    protected boolean isEnabled() {
        return this.autoServiceRegistrationProperties.isEnabled();
    }

    /**
     * Returns the {@link MultipleRegistration} used for service registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration reg = autoRegistration.getRegistration();
     * }</pre>
     *
     * @return the {@link MultipleRegistration} instance
     */
    @Override
    protected MultipleRegistration getRegistration() {
        return this.multipleRegistration;
    }

    /**
     * Returns the {@link MultipleRegistration} used for management service registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration mgmtReg = autoRegistration.getManagementRegistration();
     * }</pre>
     *
     * @return the {@link MultipleRegistration} instance for management
     */
    @Override
    protected MultipleRegistration getManagementRegistration() {
        return this.multipleRegistration;
    }
}