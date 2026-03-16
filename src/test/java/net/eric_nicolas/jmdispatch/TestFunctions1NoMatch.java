package net.eric_nicolas.jmdispatch;

import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

public class TestFunctions1NoMatch {

    @Dispatch
    public static void f_B(B b) {
    }

    private static final DispatchTable1 f_table = new DispatchTable1().autoregister(TestFunctions1NoMatch.class);

    static void f(A a) {
        f_table.dispatch(a);
    }

    @Test(expected = DispatchNoMatchException.class)
    public void test() {
        A a = new A(1);
        // no match, A is not a subtype of B => raises DispatchNoMatchException
        f(a);
    }
}
