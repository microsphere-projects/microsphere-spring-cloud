package io.microsphere.spring.cloud.client.service.registry;

import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

/**
 * @author <a href="mailto:maimengzzz@gmail.com">韩超</a>
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class CompositeAutoServiceRegistration extends AbstractAutoServiceRegistration<CompositeRegistration> {

    private final AutoServiceRegistrationProperties autoServiceRegistrationProperties;
    private final CompositeRegistration compositeRegistration;

    public CompositeAutoServiceRegistration(CompositeRegistration compositeRegistration,
                                            ServiceRegistry<CompositeRegistration> serviceRegistry,
                                            AutoServiceRegistrationProperties properties) {
        super(serviceRegistry, properties);
        this.compositeRegistration = compositeRegistration;
        this.autoServiceRegistrationProperties = properties;
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
    protected CompositeRegistration getRegistration() {
        return this.compositeRegistration;
    }

    @Override
    protected CompositeRegistration getManagementRegistration() {
        return this.compositeRegistration;
    }
}
