package io.github.microsphere.spring.cloud.context;

import io.microsphere.spring.boot.context.OnceApplicationPreparedEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import java.util.List;

import static java.util.Arrays.asList;


/**
 * Once execution {@link ApplicationPreparedEvent} {@link ApplicationListener} for Main {@link ApplicationContext}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class OnceMainApplicationPreparedEventListener extends OnceApplicationPreparedEventListener {

    private static final String BOOTSTRAP_APPLICATION_LISTENER_CLASS_NAME = "org.springframework.cloud.bootstrap.BootstrapApplicationListener";

    private static final String BOOTSTRAP_APPLICATION_LISTENER_ENABLED_PROPERTY_NAME = "spring.cloud.bootstrap.enabled";

    private static final boolean BOOTSTRAP_APPLICATION_LISTENER_PRESENT = ClassUtils.isPresent(BOOTSTRAP_APPLICATION_LISTENER_CLASS_NAME, null);

    private static final String BOOTSTRAP_CONTEXT_ID = "bootstrap";

    private static final List<String> IGNORED_CONTEXT_ID_LIST = asList(BOOTSTRAP_CONTEXT_ID);

    protected final boolean isIgnored(SpringApplication springApplication, String[] args, ConfigurableApplicationContext context) {
        String contextId = context.getId();

        if (isIgnoredContext(contextId) || !isMainApplicationContext(context)) {
            return true;
        }

        return false;
    }

    private boolean isIgnoredContext(String contextId) {
        return BOOTSTRAP_APPLICATION_LISTENER_PRESENT && IGNORED_CONTEXT_ID_LIST.contains(contextId);
    }

    protected boolean isMainApplicationContext(ConfigurableApplicationContext context) {
        boolean main = true;
        String parentId = null;
        boolean bootApplicationListenerReady = isBootApplicationListenerReady(context);

        if (bootApplicationListenerReady) {
            ApplicationContext parentContext = context.getParent();
            parentId = parentContext == null ? null : parentContext.getId();
            if (parentContext != null) {
                parentId = parentContext.getId();
                main = BOOTSTRAP_CONTEXT_ID.equals(parentId);
            }
        }

        logger.debug("Current ApplicationContext[id : '{}' , parentId : '{}'] is {}main ApplicationContext , BootstrapApplicationListener is {}",
                context.getId(), parentId, main ? "" : "not ", bootApplicationListenerReady ? "ready" : "not ready");

        return main;
    }

    private boolean isBootApplicationListenerReady(ConfigurableApplicationContext context) {
        if (BOOTSTRAP_APPLICATION_LISTENER_PRESENT) {
            Environment environment = context.getEnvironment();
            return environment.getProperty(BOOTSTRAP_APPLICATION_LISTENER_ENABLED_PROPERTY_NAME, Boolean.class, true);
        }
        return false;
    }
}
