package net.eric_nicolas.jmdispatch.api_tests.args2;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchAmbiguousException;
import net.eric_nicolas.jmdispatch.DispatchTable;
import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

public class TestAmbiguous {

    @Dispatch
    public static void f_A_Y(A a, Y x) {
    }

    @Dispatch
    public static void f_B_X(B b, X x) {
    }

    private static final DispatchTable table = DispatchTable.factory(2).autoregister(TestAmbiguous.class);

    static void f(A a, X x) {
        table.dispatch(a, x);
    }

    @Test(expected = DispatchAmbiguousException.class)
    public void test() {
        A b = new B(1, 2);
        X y = new Y(3, 4);
        // ambiguous, (B,Y)=> (A, Y) and (B, Y)= (B,X) raises DispatchAmbiguousException
        f(b, y);
    }
}
