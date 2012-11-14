package org.codeswarm.ambitiouspool;

public interface PoolLimitationStrategy {

    boolean mayCreateNewThread(ThreadPool pool);

}
