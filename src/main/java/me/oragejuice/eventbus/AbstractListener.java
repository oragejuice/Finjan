package me.oragejuice.eventbus;

import java.util.function.Consumer;

public abstract class AbstractListener implements Consumer<Object> {

    protected Class target;
    protected Object owner;
    int priority = 0;

    public Class getTarget() {
        return target;
    }

    public Object getOwner() {
        return owner;
    }
}
