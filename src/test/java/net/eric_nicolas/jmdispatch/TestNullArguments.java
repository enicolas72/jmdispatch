package net.eric_nicolas.jmdispatch;

import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

/**
 * Bug: passing null arguments to dispatch() causes NPE at .getClass().
 * Should throw DispatchNoMatchException with a clear message instead.
 */
public class TestNullArguments {

    @Dispatch
    public static void handle(A a, X x) {}

    private static final DispatchTable2 table2 = new DispatchTable2().autoregister(TestNullArguments.class);

    @Test(expected = DispatchNoMatchException.class)
    public void testNullFirstArg2() {
        table2.dispatch(null, new X(1));
    }

    @Test(expected = DispatchNoMatchException.class)
    public void testNullSecondArg2() {
        table2.dispatch(new A(1), null);
    }

    @Test(expected = DispatchNoMatchException.class)
    public void testBothNullArgs2() {
        table2.dispatch(null, null);
    }
}
