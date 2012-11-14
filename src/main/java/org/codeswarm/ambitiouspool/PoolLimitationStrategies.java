package org.codeswarm.ambitiouspool;

public final class PoolLimitationStrategies {

    private PoolLimitationStrategies() { }

    public static PoolLimitationStrategy unlimited() {
        return Unlimited.INSTANCE;
    }

    public static PoolLimitationStrategy maxPoolSize(int maxThreadCount) {
        if (maxThreadCount < 0) {
            throw new IllegalArgumentException("Max thread count cannot be negative.");
        }
        return new MaxPoolSize(maxThreadCount);
    }

}

class Unlimited implements PoolLimitationStrategy {

    static final Unlimited INSTANCE = new Unlimited();

    @Override
    public boolean mayCreateNewThread(ThreadPool pool) {
        return true;
    }

}

class MaxPoolSize implements PoolLimitationStrategy {

    private final int maxThreadCount;

    MaxPoolSize(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    @Override
    public boolean mayCreateNewThread(ThreadPool pool) {
        return pool.activeThreadCount() < maxThreadCount;
    }

}
