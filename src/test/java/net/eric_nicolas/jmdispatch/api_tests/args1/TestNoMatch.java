package net.eric_nicolas.jmdispatch.api_tests.args1;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchNoMatchException;
import net.eric_nicolas.jmdispatch.DispatchTable;
import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

public class TestNoMatch {

    @Dispatch
    public static void f_B(B b) {
    }

    private static final DispatchTable table = DispatchTable.factory(1).autoregister(TestNoMatch.class);

    static void f(A a) {
        table.dispatch(a);
    }

    @Test(expected = DispatchNoMatchException.class)
    public void test() {
        A a = new A(1);
        // no match, A is not a subtype of B => raises DispatchNoMatchException
        f(a);
    }
}
