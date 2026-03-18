package net.eric_nicolas.jmdispatch.api_tests.args2;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable;
import org.junit.Assert;
import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestReturnValues {

    // returns a String (object)
    @Dispatch
    public static String describe(A a, X x) {
        return "A=" + a.a + " X=" + x.x;
    }

    // returns a String (object), more specific
    @Dispatch
    public static String describe(B b, Y y) {
        return "B=" + b.a + "," + b.b + " Y=" + y.x + "," + y.y;
    }

    private static final DispatchTable table = DispatchTable.factory(2).autoregister(TestReturnValues.class);

    @Test
    public void testObjectReturn() {
        Object result = table.dispatch(new A(1), new X(4));
        Assert.assertEquals("A=" + 1 + " X=" + 4, result);
    }

    @Test
    public void testObjectReturnSpecific() {
        Object result = table.dispatch(new B(2, 3), new Y(5, 6));
        Assert.assertEquals("B=2,3 Y=5,6", result);
    }

    @Test
    public void testObjectReturnFallback() {
        // B dispatched to A handler, Y dispatched to X handler
        Object result = table.dispatch(new B(2, 3), new X(4));
        Assert.assertEquals("A=2 X=4", result);
    }
}
