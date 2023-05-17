import me.oragejuice.eventbus.ASMListener;
import me.oragejuice.eventbus.ASMListenerFactory;
import me.oragejuice.eventbus.InvalidFactoryName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class ASMListenerFactoryTest {

    ASMListenerFactory factory;

    {
        try {
            factory = new ASMListenerFactory("a");
        } catch (InvalidFactoryName e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("creation and calling")
    void creation() throws NoSuchMethodException {

        SubscriberClass o = new SubscriberClass();
        Method m = o.getClass().getDeclaredMethods()[0];
        ASMListener l = factory.getOrPutAndReturn(m, o);

        TestEvent event = new TestEvent(3);
        l.accept(event);
        Assertions.assertEquals(o.x, 4);

    }
}
