package org.codeswarm.ambitiouspool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class AmbitiousPoolBuilder {

    private TaskSupplier taskSupplier = TaskSuppliers.noTasks();
    private PoolLimitationStrategy limitationStrategy = PoolLimitationStrategies.unlimited();
    private ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private int corePoolSize;

    public AmbitiousPoolBuilder withTaskSupplier(TaskSupplier taskSupplier) {
        if (taskSupplier == null) {
            throw new NullPointerException();
        }
        this.taskSupplier = taskSupplier;
        return this;
    }

    public AmbitiousPoolBuilder withLimitationStrategy(PoolLimitationStrategy limitationStrategy) {
        if (limitationStrategy == null) {
            throw new NullPointerException();
        }
        this.limitationStrategy = limitationStrategy;
        return this;
    }

    public AmbitiousPoolBuilder withThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException();
        }
        this.threadFactory = threadFactory;
        return this;
    }

    public AmbitiousPoolBuilder withCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("Core pool size cannot be negative.");
        }
        this.corePoolSize = corePoolSize;
        return this;
    }

    public AmbitiousPool build() {
        return new Impl(taskSupplier, limitationStrategy, threadFactory, corePoolSize);
    }

}

class Impl implements AmbitiousPool {

    private final TaskSupplier taskSupplier;
    private final PoolLimitationStrategy limitationStrategy;
    private final ExecutorService executorService;
    private final ReentrantLock lock = new ReentrantLock();

    private boolean shutdown;
    private int activeThreadCount;

    public Impl(TaskSupplier taskSupplier, PoolLimitationStrategy limitationStrategy,
                ThreadFactory threadFactory, int corePoolSize) {

        this.taskSupplier = taskSupplier;
        this.limitationStrategy = limitationStrategy;

        executorService = new ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE,
               60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory) {

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                lock.lock();
                activeThreadCount--;
                lock.unlock();
            }
        };
    }

    @Override
    public void lookForWork() {
        lock.lock();
        try {
            boolean loop = true;
            while (loop) {
                loop = false;
                if (!shutdown && limitationStrategy.mayCreateNewThread(this)) {
                    Runnable task = taskSupplier.getTask();
                    if (task != null) {
                        executorService.submit(task);
                        activeThreadCount++;
                        loop = true;
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int activeThreadCount() {
        return activeThreadCount;
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            shutdown = true;
            executorService.shutdown();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdownNow() {
        lock.lock();
        try {
            shutdown = true;
            executorService.shutdownNow();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
    }

}
