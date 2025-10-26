package io.microsphere.spring.cloud.fault.tolerance.loadbalancer;

import java.util.StringJoiner;
import java.util.concurrent.atomic.LongAdder;

/**
 * Weighed Round-Robin
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class WeightedRoundRobin {

    private final String id;

    private volatile int weight;

    LongAdder current = new LongAdder();

    private volatile long lastUpdate;

    public WeightedRoundRobin(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
        current.reset();
    }

    public long increaseCurrent() {
        current.add(weight);
        return current.longValue();
    }

    public void sel(int total) {
        current.add(-1 * total);
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WeightedRoundRobin.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("weight=" + weight)
                .add("current=" + current)
                .add("lastUpdate=" + lastUpdate)
                .toString();
    }
}
