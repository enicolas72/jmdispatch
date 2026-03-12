package net.eric_nicolas.jmdispatch;

import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

public class TestFunctions3Ambiguous {

    @Dispatch
    public static void f_A_C_X(A a, C b, X x) {
    }

    @Dispatch
    public static void f_A_B_Y(A a, B b, Y y) {
    }

    private static final DispatchTableN f_table = new DispatchTableN(3).autoregister(TestFunctions3Ambiguous.class);

    static void f(A a, B b, X x) {
        f_table.dispatch(a, b, x);
    }

    @Test(expected = DispatchAmbiguousException.class)
    public void test() {
        A a = new A(1);
        B c = new C(4, 5, 6);
        X y = new Y(8, 9);
        // ambiguous, (A, C, Y)= (A, C, X) and (A, C, Y)= (A, B, Y)
        f(a, c, y);
    }
}
