package org.codeswarm.ambitiouspool;

public interface Shutdownable {

    /**
     * Initiates an orderly shutdown in which currently executing
     * tasks will be completed, but no new tasks will be acquired.
     */
    void shutdown();

    /**
     * Interrupts all active threads, and ceases acquiring new tasks.
     */
    void shutdownNow();

    /**
     * Blocks until all tasks have completed execution after a
     * shutdown request, or the timeout occurs, or the current
     * thread is interrupted, whichever happens first.
     */
    void awaitTermination() throws InterruptedException;

}
