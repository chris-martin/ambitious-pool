package org.codeswarm.ambitiouspool;

public interface AmbitiousPool extends ThreadPool, Shutdownable {

    void lookForWork();

}
