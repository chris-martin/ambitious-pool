package org.codeswarm.ambitiouspool;

public interface TaskSupplier {

    /**
     * A runnable, or {@code null} if no work is available.
     */
    Runnable getTask();

}
