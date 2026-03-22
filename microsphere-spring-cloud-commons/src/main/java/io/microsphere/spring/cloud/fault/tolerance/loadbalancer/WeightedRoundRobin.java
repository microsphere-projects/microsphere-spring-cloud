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
     * Constructs a new {@link WeightedRoundRobin} instance with the specified identifier.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * }</pre>
     *
     * @param id the unique identifier for this weighted round-robin entry
     */
    public WeightedRoundRobin(String id) {
        this.id = id;
    }

    /**
     * Returns the unique identifier of this {@link WeightedRoundRobin} entry.
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
     * Returns the current weight assigned to this entry.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(5);
     * int weight = wrr.getWeight(); // 5
     * }</pre>
     *
     * @return the weight value
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets the weight for this entry and resets the current accumulator to zero.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(5);
     * }</pre>
     *
     * @param weight the weight value to assign
     */
    public void setWeight(int weight) {
        this.weight = weight;
        current.reset();
    }

    /**
     * Increases the current accumulator by the configured weight and returns the updated value.
     * This is used in the weighted round-robin selection algorithm to accumulate weight
     * before selection.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(5);
     * long current = wrr.increaseCurrent(); // adds weight to current
     * }</pre>
     *
     * @return the current accumulator value after adding the weight
     */
    public long increaseCurrent() {
        current.add(weight);
        return current.longValue();
    }

    /**
     * Subtracts the given total weight from the current accumulator after this entry
     * has been selected in the weighted round-robin algorithm.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(5);
     * wrr.increaseCurrent();
     * wrr.sel(10); // subtracts total from current
     * }</pre>
     *
     * @param total the total weight of all entries to subtract from the current accumulator
     */
    public void sel(int total) {
        current.add(-1 * total);
    }

    /**
     * Returns the timestamp of the last update to this entry.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * long lastUpdate = wrr.getLastUpdate();
     * }</pre>
     *
     * @return the last update timestamp in milliseconds
     */
    public long getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the timestamp of the last update to this entry.
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
     * Returns a string representation of this {@link WeightedRoundRobin} including its
     * id, weight, current accumulator value, and last update timestamp.
     *
     * <p>Example Usage:
     * <pre>{@code
     * WeightedRoundRobin wrr = new WeightedRoundRobin("server-1");
     * wrr.setWeight(5);
     * String str = wrr.toString(); // "WeightedRoundRobin[id='server-1', weight=5, current=0, lastUpdate=0]"
     * }</pre>
     *
     * @return the string representation
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