package net.eric_nicolas.jmdispatch.api_tests.args2;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchNoMatchException;
import net.eric_nicolas.jmdispatch.DispatchTable;
import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

public class TestNoMatch {

    @Dispatch
    public static void f_A_Y(A a, Y x) {
    }

    @Dispatch
    public static void f_B_Y(B b, Y y) {
    }

    private static final DispatchTable table = DispatchTable.factory(2).autoregister(TestNoMatch.class);

    static void f(A a, X x) {
        table.dispatch(a, x);
    }

    @Test(expected = DispatchNoMatchException.class)
    public void test() {
        A a = new A(1);
        X x = new X(4);
        // no match, raises DispatchNoMatchException
        f(a, x);
    }
}
