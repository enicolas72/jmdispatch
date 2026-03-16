package net.eric_nicolas.jmdispatch;

import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestNullArguments1 {

    @Dispatch
    public static void handle(A a) {}

    private static final DispatchTable1 table = new DispatchTable1().autoregister(TestNullArguments1.class);

    @Test(expected = DispatchNoMatchException.class)
    public void testNullArg() {
        table.dispatch(null);
    }
}
