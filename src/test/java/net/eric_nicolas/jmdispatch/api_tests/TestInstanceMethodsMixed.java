package net.eric_nicolas.jmdispatch.api_tests;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable;
import net.eric_nicolas.jmdispatch.InvalidDispatchException;
import org.junit.Assert;
import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestInstanceMethodsMixed {

    // Class with both static and instance @Dispatch methods
    public static class MixedHandler {
        private final String tag;

        MixedHandler(String tag) {
            this.tag = tag;
        }

        @Dispatch
        public String handleInstance(A a, X x) {
            return tag + ":A-X";
        }

        @Dispatch
        public static String handleStatic(B b, Y y) {
            return "static:B-Y";
        }
    }

    @Test
    public void testMixedStaticAndInstance() {
        DispatchTable table = DispatchTable.factory(2).autoregister(new MixedHandler("inst"));

        Object r1 = table.dispatch(new A(1), new X(2));
        Assert.assertEquals("inst:A-X", r1);

        Object r2 = table.dispatch(new B(1, 2), new Y(3, 4));
        Assert.assertEquals("static:B-Y", r2);
    }

    // Verify that registering instance methods via Class (not instance) throws
    public static class InstanceOnlyHandler {
        @Dispatch
        public String handle(A a, X x) {
            return "oops";
        }
    }

    @Test(expected = InvalidDispatchException.class)
    public void testInstanceMethodWithClassRegistrationThrows() {
        DispatchTable.factory(2).autoregister(InstanceOnlyHandler.class);
    }

    // Handler returning void
    public static class VoidHandler {
        int callCount = 0;

        @Dispatch
        public void handle(A a, X x) {
            callCount++;
        }
    }

    @Test
    public void testInstanceVoidReturn() {
        VoidHandler handler = new VoidHandler();
        DispatchTable table = DispatchTable.factory(2).autoregister(handler);

        table.dispatch(new A(1), new X(2));
        table.dispatch(new A(3), new X(4));

        Assert.assertEquals(2, handler.callCount);
    }

    // Handler returning primitive
    public static class PrimitiveHandler {
        private final int multiplier;

        PrimitiveHandler(int multiplier) {
            this.multiplier = multiplier;
        }

        @Dispatch
        public int compute(A a, X x) {
            return (a.a + x.x) * multiplier;
        }
    }

    @Test
    public void testInstancePrimitiveReturn() {
        DispatchTable table = DispatchTable.factory(2).autoregister(new PrimitiveHandler(10));

        Object result = table.dispatch(new A(3), new X(7));
        Assert.assertEquals(100, result);
    }
}
