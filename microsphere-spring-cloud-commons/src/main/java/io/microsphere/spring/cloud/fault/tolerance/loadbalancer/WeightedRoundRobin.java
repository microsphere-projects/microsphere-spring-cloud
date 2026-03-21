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

    /**
     * Create a new {@link WeightedRoundRobin} instance with the given identifier.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(5);
     * }</pre>
     *
     * @param id the unique identifier for this weighted round-robin entry
     */
    public WeightedRoundRobin(String id) {
        this.id = id;
    }

    /**
     * Get the unique identifier for this {@link WeightedRoundRobin} entry.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * String id = wrr.getId(); // "server-1"
     * }</pre>
     *
     * @return the identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Get the current weight of this {@link WeightedRoundRobin} entry.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(5);
     * int weight = wrr.getWeight(); // 5
     * }</pre>
     *
     * @return the current weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Set the weight for this {@link WeightedRoundRobin} entry and reset the current counter.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(10);
     * }</pre>
     *
     * @param weight the new weight value
     */
    public void setWeight(int weight) {
        this.weight = weight;
        current.reset();
    }

    /**
     * Increase the current counter by the weight value and return the updated value.
     * Used during weighted round-robin selection to accumulate the weight for this entry.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(5);
     * long current = wrr.increaseCurrent(); // 5
     * current = wrr.increaseCurrent();      // 10
     * }</pre>
     *
     * @return the updated current counter value
     */
    public long increaseCurrent() {
        current.add(weight);
        return current.longValue();
    }

    /**
     * Subtract the total weight from the current counter after this entry has been selected.
     * This is part of the weighted round-robin algorithm to reduce the selected entry's counter.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(5);
     * wrr.increaseCurrent();
     * wrr.sel(10); // subtract total weight of all entries
     * }</pre>
     *
     * @param total the total weight of all entries to subtract
     */
    public void sel(int total) {
        current.add(-1 * total);
    }

    /**
     * Get the timestamp of the last update to this {@link WeightedRoundRobin} entry.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setLastUpdate(System.currentTimeMillis());
     * long lastUpdate = wrr.getLastUpdate();
     * }</pre>
     *
     * @return the last update timestamp in milliseconds
     */
    public long getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Set the timestamp of the last update to this {@link WeightedRoundRobin} entry.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setLastUpdate(System.currentTimeMillis());
     * }</pre>
     *
     * @param lastUpdate the last update timestamp in milliseconds
     */
    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Returns a string representation of this {@link WeightedRoundRobin} including
     * its id, weight, current counter, and last update timestamp.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(5);
     * String s = wrr.toString(); // "WeightedRoundRobin[id='server-1', weight=5, current=0, lastUpdate=0]"
     * }</pre>
     *
     * @return a string representation of this entry
     */
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