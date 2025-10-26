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

    public MultipleAutoServiceRegistration(MultipleRegistration multipleRegistration,
                                           ServiceRegistry<MultipleRegistration> serviceRegistry,
                                           AutoServiceRegistrationProperties properties) {
        super(serviceRegistry, properties);
        this.autoServiceRegistrationProperties = properties;
        this.multipleRegistration = multipleRegistration;
    }

    @Override
    protected Object getConfiguration() {
        return null;
    }

    @Override
    protected boolean isEnabled() {
        return this.autoServiceRegistrationProperties.isEnabled();
    }

    @Override
    protected MultipleRegistration getRegistration() {
        return this.multipleRegistration;
    }

    @Override
    protected MultipleRegistration getManagementRegistration() {
        return this.multipleRegistration;
    }
}
