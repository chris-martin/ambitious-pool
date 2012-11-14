package org.codeswarm.ambitiouspool;

public final class TaskSuppliers {

    private TaskSuppliers() { }

    public static TaskSupplier noTasks() {
        return Nil.INSTANCE;
    }

}

class Nil implements TaskSupplier {

    static final Nil INSTANCE = new Nil();

    @Override
    public Runnable getTask() {
        return null;
    }

}
