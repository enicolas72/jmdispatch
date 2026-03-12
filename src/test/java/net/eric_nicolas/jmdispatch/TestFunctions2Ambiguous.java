package net.eric_nicolas.jmdispatch;

import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

public class TestFunctions2Ambiguous {

    @Dispatch
    public static void f_A_Y(A a, Y x) {
    }

    @Dispatch
    public static void f_B_X(B b, X x) {
    }

    private static final DispatchTable2 f_table = new DispatchTable2().autoregister(TestFunctions2Ambiguous.class);

    static void f(A a, X x) {
        f_table.dispatch(a, x);
    }

    @Test(expected = DispatchAmbiguousException.class)
    public void test() {
        A b = new B(1, 2);
        X y = new Y(3, 4);
        // ambiguous, (B,Y)=> (A, Y) and (B, Y)= (B,X) raises DispatchAmbiguousException
        f(b, y);
    }
}
