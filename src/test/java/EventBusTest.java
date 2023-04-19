import me.oragejuice.eventbus.EventManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EventBusTest {

    @Test
    @DisplayName("subscribe and call")
    void subscribeAndCall() {
        EventManager bus = new EventManager();
        SubscriberClass s = new SubscriberClass();
        bus.subscribe(s);

        TestEvent event = new TestEvent(2);
        bus.post(event);

        Assertions.assertEquals(3, event.i);

        TestEvent event2 = new TestEvent(3);
        bus.post(event);

        Assertions.assertEquals(4, event.i);
        Assertions.assertEquals(7, s.x);

    }

    @Test
    @DisplayName("unsubscribe")
    void unsubscribe() {
        EventManager bus = new EventManager();
        SubscriberClass s = new SubscriberClass();
        bus.subscribe(s);

        bus.unsubscribe(s);

        TestEvent event = new TestEvent(2);
        bus.post(event);
        Assertions.assertEquals(2, event.i);
    }

    @Test
    @DisplayName("Speed")
    void speed() {
        EventManager bus = new EventManager();
        SubscriberClass s = new SubscriberClass();
        bus.subscribe(s);

        TestEvent event = new TestEvent(2);
        long time = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            bus.post(event);
        }
        long total = System.nanoTime() - time;
        long average = total / 1_000_000;
        System.out.println("average " + average + "ns");
        System.out.println("total " + Math.round(total / 1_000_000.) + "ms");


    }
}
