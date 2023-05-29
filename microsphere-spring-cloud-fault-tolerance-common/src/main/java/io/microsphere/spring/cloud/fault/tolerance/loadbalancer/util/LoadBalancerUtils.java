package io.microsphere.spring.cloud.fault.tolerance.loadbalancer.util;

/**
 * The utilities class of Load Balancer
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class LoadBalancerUtils {

    private LoadBalancerUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Calculate the weight according to the uptime proportion of warmup time
     * the new weight will be within 1(inclusive) to weight(inclusive)
     *
     * @param uptime the uptime in milliseconds
     * @param warmup the warmup time in milliseconds
     * @param weight the weight of an invoker
     * @return weight which takes warmup into account
     */
    public static int calculateWarmupWeight(long uptime, long warmup, int weight) {
        int ww = (int) (Math.round(Math.pow((uptime / (double) warmup), 2) * weight));
        return ww < 1 ? 1 : (Math.min(ww, weight));
    }
}
