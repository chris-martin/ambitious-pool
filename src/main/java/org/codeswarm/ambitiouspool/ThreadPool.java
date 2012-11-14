package org.codeswarm.ambitiouspool;

public interface ThreadPool {

    /**
     * The number of threads that are currently active.
     */
    int activeThreadCount();

}
