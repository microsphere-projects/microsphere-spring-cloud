package io.microsphere.spring.cloud.fault.tolerance.constants;

import io.microsphere.annotation.ConfigurationProperty;

import java.util.concurrent.TimeUnit;

import static io.microsphere.annotation.ConfigurationProperty.APPLICATION_SOURCE;
import static io.microsphere.spring.cloud.commons.constants.CommonsPropertyConstants.MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX;

/**
 * Fault-Tolerance Property Constants
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface FaultTolerancePropertyConstants {

    /**
     * Property name prefix
     */
    String FAULT_TOLERANCE_PROPERTY_NAME_PREFIX = MICROSPHERE_SPRING_CLOUD_PROPERTY_NAME_PREFIX + "fault-tolerance.";

    /**
     * Load Balancers' property name prefix
     */
    String LOAD_BALANCER_PROPERTY_PREFIX = FAULT_TOLERANCE_PROPERTY_NAME_PREFIX + "load-balancer.";

    /**
     * The metadata name of warm-up time
     */
    @ConfigurationProperty(
            type = long.class,
            defaultValue = "600000",
            source = APPLICATION_SOURCE
    )
    String WARMUP_TIME_PROPERTY_NAME = FAULT_TOLERANCE_PROPERTY_NAME_PREFIX + "warmup-time";

    /**
     * The property name of weight
     */
    @ConfigurationProperty(
            type = int.class,
            defaultValue = "100",
            source = APPLICATION_SOURCE
    )
    String WEIGHT_PROPERTY_NAME = FAULT_TOLERANCE_PROPERTY_NAME_PREFIX + "weight";

    /**
     * The default property value of warm-up time ：10 minutes
     */
    long DEFAULT_WARMUP_TIME_PROPERTY_VALUE = TimeUnit.MINUTES.toMillis(10);

    /**
     * The default property value of weight : 100
     */
    int DEFAULT_WEIGHT_PROPERTY_VALUE = 100;

}
