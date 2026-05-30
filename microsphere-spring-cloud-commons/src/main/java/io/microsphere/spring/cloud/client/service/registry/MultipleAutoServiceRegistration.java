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
     * Constructs a new {@link MultipleAutoServiceRegistration} with the specified
     * {@link MultipleRegistration}, {@link ServiceRegistry}, and
     * {@link AutoServiceRegistrationProperties}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleRegistration registration = new MultipleRegistration(registrations);
     * ServiceRegistry<MultipleRegistration> serviceRegistry = new InMemoryServiceRegistry();
     * AutoServiceRegistrationProperties properties = new AutoServiceRegistrationProperties();
     * MultipleAutoServiceRegistration autoReg =
     *     new MultipleAutoServiceRegistration(registration, serviceRegistry, properties);
     * }</pre>
     *
     * @param multipleRegistration the {@link MultipleRegistration} to manage
     * @param serviceRegistry      the {@link ServiceRegistry} to delegate to
     * @param properties           the {@link AutoServiceRegistrationProperties} for configuration
     */
    public MultipleAutoServiceRegistration(MultipleRegistration multipleRegistration,
                                           ServiceRegistry<MultipleRegistration> serviceRegistry,
                                           AutoServiceRegistrationProperties properties) {
        super(serviceRegistry, properties);
        this.autoServiceRegistrationProperties = properties;
        this.multipleRegistration = multipleRegistration;
    }

    /**
     * Returns the configuration object for this auto service registration.
     * This implementation always returns {@code null}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleAutoServiceRegistration autoReg = ...;
     * Object config = autoReg.getConfiguration(); // null
     * }</pre>
     *
     * @return {@code null}
     */
    @Override
    protected Object getConfiguration() {
        return null;
    }

    /**
     * Determines whether this auto service registration is enabled based on the
     * {@link AutoServiceRegistrationProperties}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleAutoServiceRegistration autoReg = ...;
     * boolean enabled = autoReg.isEnabled();
     * }</pre>
     *
     * @return {@code true} if auto service registration is enabled
     */
    @Override
    protected boolean isEnabled() {
        return this.autoServiceRegistrationProperties.isEnabled();
    }

    /**
     * Returns the {@link MultipleRegistration} managed by this auto service registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleAutoServiceRegistration autoReg = ...;
     * MultipleRegistration registration = autoReg.getRegistration();
     * }</pre>
     *
     * @return the {@link MultipleRegistration} instance
     */
    @Override
    protected MultipleRegistration getRegistration() {
        return this.multipleRegistration;
    }

    /**
     * Returns the management {@link MultipleRegistration}, which is the same as
     * the primary registration in this implementation.
     *
     * <p>Example Usage:
     * <pre>{@code
     * MultipleAutoServiceRegistration autoReg = ...;
     * MultipleRegistration mgmtRegistration = autoReg.getManagementRegistration();
     * }</pre>
     *
     * @return the {@link MultipleRegistration} instance used for management
     */
    @Override
    protected MultipleRegistration getManagementRegistration() {
        return this.multipleRegistration;
    }
}