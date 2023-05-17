# Finjan Event bus

## What is an event bus?
*Finjan* is an event bus that allows for post/subscribe style communication between components.
They're designed to replace large condition managers to call groups of methods. 
Listeners are subscribed to the event which then are called when events are posted.

## Why use Finjan?
Finjan is a performant and lightweight event bus that simplifies code, 
that doesn't heavily rely on java's expensive reflection operations thus making it far more
performant than other popular event busses. In exchange for less runtime safety guarantees.
- simple and lightweight
- performant
- minimal dependencies
Finjan uses runtime class generation with ow2-asm to create invokers to call methods, 
which makes it significantly faster than others. However currently it does not contain as 
many features or safety guarantees that others do.

## How to use Finjan

1. Defining an event listener
```java
class EventBusListener {
    
    //This method will now listen to all RecordUpdateEvent events
    @EventHandler
    public void onRecordUpdate(RecordUpdateEvent event) {
        event.getValue();
        //...
    }
}
```

2. Instantiating an event bus
```java
EventManager eventBus = new EventManager();
```

3. subscribe/unsubscribe an instance of the listener
```java
eventBus.subscribe(listener);
eventBus.unsubscribe(listener);

```

4. Posting an event
```java
eventBus.post(new RecordUpdateEvent(...));
```