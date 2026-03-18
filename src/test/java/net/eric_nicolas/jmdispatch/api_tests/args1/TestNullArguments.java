package net.eric_nicolas.jmdispatch.api_tests.args1;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchNoMatchException;
import net.eric_nicolas.jmdispatch.DispatchTable;
import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestNullArguments {

    @Dispatch
    public static void handle(A a) {}

    private static final DispatchTable table = DispatchTable.factory(1).autoregister(TestNullArguments.class);

    @Test(expected = DispatchNoMatchException.class)
    public void testNullArg() {
        table.dispatch(null);
    }
}
