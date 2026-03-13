package net.eric_nicolas.jmdispatch;

import org.junit.Assert;
import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestReturnValuesPrimitive {

    @Dispatch
    public static int add(A a, X x) {
        return a.a + x.x;
    }

    @Dispatch
    public static double multiply(B b, Y y) {
        return b.b * y.y;
    }

    @Dispatch
    public static boolean check(A a, Y y) {
        return a.a > y.y;
    }

    private static final DispatchTable2 table = new DispatchTable2().autoregister(TestReturnValuesPrimitive.class);

    @Test
    public void testIntReturn() {
        Object result = table.dispatch(new A(3), new X(7));
        Assert.assertEquals(10, result);
    }

    @Test
    public void testDoubleReturn() {
        Object result = table.dispatch(new B(2, 5), new Y(3, 4));
        Assert.assertEquals(20.0, result);
    }

    @Test
    public void testBooleanReturn() {
        Object result = table.dispatch(new A(10), new Y(3, 2));
        Assert.assertEquals(true, result);
    }
}
