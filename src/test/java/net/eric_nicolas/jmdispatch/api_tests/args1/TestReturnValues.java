package net.eric_nicolas.jmdispatch.api_tests.args1;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable;
import org.junit.Assert;
import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestReturnValues {

    // returns a String (object)
    @Dispatch
    public static String describe(A a) {
        return "A=" + a.a;
    }

    // returns a String (object), more specific
    @Dispatch
    public static String describe(B b) {
        return "B=" + b.a + "," + b.b;
    }

    private static final DispatchTable table = DispatchTable.factory(1).autoregister(TestReturnValues.class);

    @Test
    public void testObjectReturn() {
        Object result = table.dispatch(new A(1));
        Assert.assertEquals("A=1", result);
    }

    @Test
    public void testObjectReturnSpecific() {
        Object result = table.dispatch(new B(2, 3));
        Assert.assertEquals("B=2,3", result);
    }

    @Test
    public void testObjectReturnFallback() {
        // C is subtype of B => dispatches to describe(B)
        Object result = table.dispatch(new C(4, 5, 6));
        Assert.assertEquals("B=4,5", result);
    }
}
