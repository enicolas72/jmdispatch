package net.eric_nicolas.jmdispatch.api_tests.args3;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchNoMatchException;
import net.eric_nicolas.jmdispatch.DispatchTable;
import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

public class TestNoMatch {

    @Dispatch
    public static void f_A_B_X(A a, B b, X x) {
    }

    @Dispatch
    public static void f_A_C_Y(A a, C c, Y y) {
    }

    private static final DispatchTable table = DispatchTable.factory(3).autoregister(TestNoMatch.class);

    static void f(A a, A b, X x) {
        table.dispatch(a, b, x);
    }

    @Test(expected = DispatchNoMatchException.class)
    public void test() {
        A a = new A(1);
        X x = new X(7);
        // no match, raises DispatchNoMatchException
        f(a, a, x);
    }
}
