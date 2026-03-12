package net.eric_nicolas.jmdispatch;

import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

public class TestFunctions2NoMatch {

    @Dispatch
    public static void f_A_Y(A a, Y x) {
    }

    @Dispatch
    public static void f_B_Y(B b, Y y) {
    }

    private static final DispatchTable2 f_table = new DispatchTable2().autoregister(TestFunctions2NoMatch .class);

    static void f(A a, X x) {
        f_table.dispatch(a, x);
    }

    @Test(expected = DispatchNoMatchException.class)
    public void test() {
        A a = new A(1);
        X x = new X(4);
        // no match, raises DispatchNoMatchException
        f(a, x);
    }
}
