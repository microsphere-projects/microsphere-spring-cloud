package io.microsphere.spring.cloud.client.service.registry.endpoint;

import io.microsphere.logging.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.context.ApplicationListener;

import static io.microsphere.logging.LoggerFactory.getLogger;

/**
 * Abstract Endpoint for Service Registration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractServiceRegistrationEndpoint implements SmartInitializingSingleton, ApplicationListener<WebServerInitializedEvent> {

    protected final Logger logger = getLogger(getClass());

    @Value("${spring.application.name}")
    protected String applicationName;

    @Autowired
    private ObjectProvider<Registration> registrationProvider;

    @Autowired
    private ObjectProvider<ServiceRegistry> serviceRegistryProvider;

    @Autowired
    private ObjectProvider<AbstractAutoServiceRegistration> autoServiceRegistrationProvider;

    protected Registration registration;

    protected ServiceRegistry serviceRegistry;

    protected AbstractAutoServiceRegistration serviceRegistration;

    protected int port;

    protected static boolean running;

    /**
     * {@inheritDoc}
     * <p>Initializes the {@link Registration}, {@link ServiceRegistry}, and
     * {@link AbstractAutoServiceRegistration} from available bean providers.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Called automatically by the Spring container after all singletons are instantiated.
     * // Ensures registration, serviceRegistry, and serviceRegistration fields are populated.
     * }</pre>
     */
    @Override
    public void afterSingletonsInstantiated() {
        this.registration = registrationProvider.getIfAvailable();
        this.serviceRegistry = serviceRegistryProvider.getIfAvailable();
        this.serviceRegistration = autoServiceRegistrationProvider.getIfAvailable();
    }

    /**
     * {@inheritDoc}
     * <p>Captures the web server port and detects the running state of the
     * {@link AbstractAutoServiceRegistration}.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Called automatically when the embedded web server has been initialized.
     * // After this event, the port and running state are available.
     * }</pre>
     *
     * @param event the {@link WebServerInitializedEvent} carrying the initialized web server
     */
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        WebServer webServer = event.getWebServer();
        this.port = webServer.getPort();
        this.running = detectRunning(serviceRegistration);
    }

    /**
     * Detects whether the given {@link AbstractAutoServiceRegistration} is currently running.
     *
     * <p>Example Usage:
     * <pre>{@code
     * boolean running = AbstractServiceRegistrationEndpoint.detectRunning(serviceRegistration);
     * }</pre>
     *
     * @param serviceRegistration the {@link AbstractAutoServiceRegistration} to check, may be {@code null}
     * @return {@code true} if the service registration is running, {@code false} otherwise
     */
    static boolean detectRunning(AbstractAutoServiceRegistration serviceRegistration) {
        return serviceRegistration == null ? false : serviceRegistration.isRunning();
    }

    /**
     * Returns whether the service registration is currently running.
     *
     * <p>Example Usage:
     * <pre>{@code
     * if (endpoint.isRunning()) {
     *     // service is registered and running
     * }
     * }</pre>
     *
     * @return {@code true} if the service registration is running, {@code false} otherwise
     */
    protected boolean isRunning() {
        return running;
    }

    /**
     * Sets the running state of the service registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * endpoint.setRunning(true);  // mark service as running
     * endpoint.setRunning(false); // mark service as stopped
     * }</pre>
     *
     * @param running {@code true} to mark the service as running, {@code false} otherwise
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}