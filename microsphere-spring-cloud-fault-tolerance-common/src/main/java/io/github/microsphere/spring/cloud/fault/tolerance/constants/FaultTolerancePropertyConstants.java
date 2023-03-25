package io.github.microsphere.spring.cloud.fault.tolerance.constants;

import java.util.concurrent.TimeUnit;

/**
 * Fault-Tolerance Property Constants
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface FaultTolerancePropertyConstants {

    /**
     * The property name of "enabled"
     */
    String ENABLED_PROPERTY_NAME = "enabled";

    /**
     * Property name prefix
     */
    String PROPERTY_NAME_PREFIX = "microsphere.fault-tolerance.";

    /**
     * Load Balancers' property name prefix
     */
    String LOAD_BALANCER_PROPERTY_PREFIX = PROPERTY_NAME_PREFIX + "load-balancer.";

    /**
     * The metadata name of management
     */
    String MANAGEMENT_PORT_METADATA_NAME = "management-port";

    /**
     * The metadata name of start time
     */
    String START_TIME_METADATA_NAME = "start-time";

    /**
     * The metadata name of warm-up time
     */
    String WARMUP_TIME_PROPERTY_NAME = PROPERTY_NAME_PREFIX + "warmup-time";

    /**
     * The property name of weight
     */
    String WEIGHT_PROPERTY_NAME = PROPERTY_NAME_PREFIX + "weight";

    /**
     * The default property value of warm-up time ：10 minutes
     */
    long DEFAULT_WARMUP_TIME_PROPERTY_VALUE = TimeUnit.MINUTES.toMillis(10);

    /**
     * The default property value of weight : 100
     */
    int DEFAULT_WEIGHT_PROPERTY_VALUE = 100;

}
