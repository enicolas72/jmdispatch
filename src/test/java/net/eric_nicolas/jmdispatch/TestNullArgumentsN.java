package net.eric_nicolas.jmdispatch;

import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

/**
 * Bug: passing null arguments to DispatchTableN.dispatch() causes NPE.
 */
public class TestNullArgumentsN {

    @Dispatch
    public static void handle(A a, X x) {}

    private static final DispatchTableN tableN = new DispatchTableN(2).autoregister(TestNullArgumentsN.class);

    @Test(expected = DispatchNoMatchException.class)
    public void testNullArgN() {
        tableN.dispatch(null, new X(1));
    }
}
