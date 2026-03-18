package net.eric_nicolas.jmdispatch.api_tests.args1;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable;
import net.eric_nicolas.jmdispatch.TestUtils;
import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestStaticMethods {

    static PrintStream out;

    @Dispatch
    public static void f_A(A a) {
        out.println("A=" + a.a);
    }

    @Dispatch
    public static void f_B(B b) {
        out.println("B=" + b.a + "," + b.b);
    }

    private static final DispatchTable table = DispatchTable.factory(1).autoregister(TestStaticMethods.class);

    static void f(A a) {
        table.dispatch(a);
    }

    @Test
    public void test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);
        A a = new A(1);
        A b = new B(2, 3);
        C c = new C(4, 5, 6);

        f(a); // dispatch to f_A
        f(b); // dispatch to f_B
        f(c); // dispatch to f_B

        out.close();
        String result = baos.toString();
        String expected = "A=1\nB=2,3\nB=4,5\n";
        TestUtils.assertEquals(expected, result);
    }
}
