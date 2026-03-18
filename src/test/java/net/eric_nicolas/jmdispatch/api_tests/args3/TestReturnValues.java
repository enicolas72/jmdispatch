package net.eric_nicolas.jmdispatch.api_tests.args3;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable;
import org.junit.Assert;
import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestReturnValues {

    @Dispatch
    public static String concat(A a, B b, X x) {
        return a.a + "+" + b.b + "+" + x.x;
    }

    @Dispatch
    public static int sum(A a, C c, Y y) {
        return a.a + c.c + y.y;
    }

    private static final DispatchTable table = DispatchTable.factory(3).autoregister(TestReturnValues.class);

    @Test
    public void testObjectReturnN() {
        Object result = table.dispatch(new A(1), new B(2, 3), new X(4));
        Assert.assertEquals("1+3+4", result);
    }

    @Test
    public void testPrimitiveReturnN() {
        Object result = table.dispatch(new A(10), new C(20, 30, 40), new Y(50, 60));
        Assert.assertEquals(110, result);
    }

    @Test
    public void testFallbackReturnN() {
        // C extends B, falls back to concat handler
        Object result = table.dispatch(new A(1), new C(2, 3, 4), new X(5));
        Assert.assertEquals("1+3+5", result);
    }
}
