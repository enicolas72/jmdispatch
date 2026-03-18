package net.eric_nicolas.jmdispatch.api_tests.args2;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchNoMatchException;
import net.eric_nicolas.jmdispatch.DispatchTable;
import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

/**
 * Bug: passing null arguments to dispatch() causes NPE at .getClass().
 * Should throw DispatchNoMatchException with a clear message instead.
 */
public class TestNullArguments {

    @Dispatch
    public static void handle(A a, X x) {}

    private static final DispatchTable table2 = DispatchTable.factory(2).autoregister(TestNullArguments.class);

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
