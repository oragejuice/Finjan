package me.oragejuice.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class EventManager {

    private final HashMap<Class<?>, ArrayList<AbstractListener>> listenerMap = new HashMap<>();
    private final HashMap<Object, ArrayList<AbstractListener>> parentMap = new HashMap<>();
    private final ASMListenerFactory listenerFactory;

    public EventManager() {
        try {
            this.listenerFactory = new ASMListenerFactory();
        } catch (InvalidFactoryName e) {
            throw new RuntimeException(e);
        }
    }

    public EventManager(String name) {
        try {
            this.listenerFactory = new ASMListenerFactory(name);
        } catch (InvalidFactoryName e) {
            throw new RuntimeException(e);
        }

    }

    public void post(Object event) {
        final ArrayList<AbstractListener> listenerList = listenerMap.get(event.getClass());
        Iterator<AbstractListener> iterator = listenerList.iterator();
        while (iterator.hasNext()) {
            iterator.next().accept(event);
        }
    }

    public void subscribe(Object obj) {
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EventHandler.class) && method.getParameterCount() == 1) {
                registerListener(obj, method, method.getAnnotation(EventHandler.class).value());
            }
        }


    }

    private void registerListener(Object object, Method method, int priority) {
        ASMListener listener = listenerFactory.getOrPutAndReturn(method, object);
        listener.priority = priority;

        //if event is not in hashmap, add it and create an arraylist to store its corresponding listeners
        if (!listenerMap.containsKey(listener.target)) {
            listenerMap.put(listener.target, new ArrayList<>());
        }

        List<AbstractListener> listenerList = listenerMap.get(listener.target);
        listenerList.add(listener);

        //sort by priority so listeners with lower priority are called first
        listenerList.sort((a, b) -> b.priority - a.priority);

    }

    public void unsubscribe(Object owner) {
        Method[] methods = owner.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EventHandler.class) && method.getParameterCount() == 1) {

                Class<?> eventClass = method.getParameterTypes()[0];
                Iterator<AbstractListener> it = listenerMap.get(eventClass).iterator();
                while (it.hasNext()) {
                    AbstractListener l = it.next();
                    if (l.owner == owner) {
                        it.remove();
                    }
                }
            }
        }
    }
}
