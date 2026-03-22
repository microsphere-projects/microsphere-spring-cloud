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
     * Initializes the {@link #registration}, {@link #serviceRegistry}, and
     * {@link #serviceRegistration} fields from the application context after
     * all singleton beans have been instantiated.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Called automatically by the Spring container:
     * endpoint.afterSingletonsInstantiated();
     * }</pre>
     */
    @Override
    public void afterSingletonsInstantiated() {
        this.registration = registrationProvider.getIfAvailable();
        this.serviceRegistry = serviceRegistryProvider.getIfAvailable();
        this.serviceRegistration = autoServiceRegistrationProvider.getIfAvailable();
    }

    /**
     * Handles the {@link WebServerInitializedEvent} to record the web server port
     * and detect whether the service registration is currently running.
     *
     * <p>Example Usage:
     * <pre>{@code
     * // Called automatically by Spring when the web server is initialized:
     * endpoint.onApplicationEvent(event);
     * }</pre>
     *
     * @param event the {@link WebServerInitializedEvent} containing the web server information
     */
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        WebServer webServer = event.getWebServer();
        this.port = webServer.getPort();
        this.running = detectRunning(serviceRegistration);
    }

    static boolean detectRunning(AbstractAutoServiceRegistration serviceRegistration) {
        return serviceRegistration == null ? false : serviceRegistration.isRunning();
    }

    /**
     * Returns whether the service registration is currently running.
     *
     * <p>Example Usage:
     * <pre>{@code
     * if (endpoint.isRunning()) {
     *     // service is registered and active
     * }
     * }</pre>
     *
     * @return {@code true} if the service registration is running, {@code false} otherwise
     */
    protected boolean isRunning() {
        return running;
    }

    /**
     * Sets the running status flag for the service registration.
     *
     * <p>Example Usage:
     * <pre>{@code
     * endpoint.setRunning(true);  // mark as running after registration
     * endpoint.setRunning(false); // mark as stopped after deregistration
     * }</pre>
     *
     * @param running {@code true} to mark the service as running, {@code false} otherwise
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}