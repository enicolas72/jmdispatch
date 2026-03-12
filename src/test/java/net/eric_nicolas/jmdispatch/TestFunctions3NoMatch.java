package net.eric_nicolas.jmdispatch;

import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

public class TestFunctions3NoMatch {

    @Dispatch
    public static void f_A_B_X(A a, B b, X x) {
    }

    @Dispatch
    public static void f_A_C_Y(A a, C c, Y y) {
    }

    private static final DispatchTableN f_table = new DispatchTableN(3).autoregister(TestFunctions3NoMatch.class);

    static void f(A a, A b, X x) {
        f_table.dispatch(a, b, x);
    }

    @Test(expected = DispatchNoMatchException.class)
    public void test() {
        A a = new A(1);
        X x = new X(7);
        // no match, raises DispatchNoMatchException
        f(a, a, x);
    }
}
