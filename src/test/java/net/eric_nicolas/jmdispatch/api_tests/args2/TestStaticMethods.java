package net.eric_nicolas.jmdispatch.api_tests.args2;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable;
import net.eric_nicolas.jmdispatch.TestUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestStaticMethods {

    static PrintStream out;

    @Dispatch
    public static void f_A_X(A a, X x) {
        out.println("A=" + a.a + " X=" + x.x);
    }

    @Dispatch
    public static void f_B_Y(B b, Y y) {
        out.println("B=" + b.a + "," + b.b + " Y=" + y.x + "," + y.y);
    }

    private static final DispatchTable table = DispatchTable.factory(2).autoregister(TestStaticMethods.class);

    static void f(A a, X x) {
        table.dispatch(a, x);
    }

    @Test
    public void test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);
        A a = new A(1);
        A b = new B(2, 3);
        X x = new X(4);
        X y = new Y(5, 6);

        f(a, x); // dispatch to f_A_X
        f(b, x); // fallback dispatch to f_A_X
        f(a, y); // fallback dispatch to f_A_X
        f(b, y); // dispatch to f_B_Y

        out.close();
        String result = baos.toString();
        String expected = "A=1 X=4\nA=2 X=4\nA=1 X=5\nB=2,3 Y=5,6\n";
        TestUtils.assertEquals(expected, result);
    }
}
