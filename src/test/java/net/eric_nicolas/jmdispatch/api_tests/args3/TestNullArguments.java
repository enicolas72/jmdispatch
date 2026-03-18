package net.eric_nicolas.jmdispatch.api_tests.args3;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchNoMatchException;
import net.eric_nicolas.jmdispatch.DispatchTable;
import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

/**
 * Bug: passing null arguments to DispatchTableN.dispatch() causes NPE.
 */
public class TestNullArguments {

    @Dispatch
    public static void handle(A a, B b, X x) {}

    private static final DispatchTable table = DispatchTable.factory(3).autoregister(TestNullArguments.class);

    @Test(expected = DispatchNoMatchException.class)
    public void testNullArgN() {
        table.dispatch(null, new B(1, 2), new X(3));
    }
}
