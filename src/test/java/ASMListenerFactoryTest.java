import me.oragejuice.eventbus.ASMListener;
import me.oragejuice.eventbus.ASMListenerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ASMListenerFactoryTest {

    ASMListenerFactory factory = new ASMListenerFactory("a");

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
