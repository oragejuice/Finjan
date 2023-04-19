import me.oragejuice.eventbus.EventHandler;

public class SubscriberClass {

    int x = 0;
    @EventHandler(1)
    public void call(TestEvent event) {
        event.i += 1;
        x += event.i;
    }
}
