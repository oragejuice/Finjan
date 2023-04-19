package me.oragejuice.eventbus;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class ASMListener extends AbstractListener {

    EventInvoker invoker;

    Method method;

    public ASMListener(Object owner, Method method, EventInvoker invoker){
        this.owner = owner;
        this.method = method;
        this.target = method.getParameterTypes()[0];
        this.invoker = invoker;

    }

    @Override
    public void accept(Object event) {
        invoker.invoke(owner, event);
    }

}
