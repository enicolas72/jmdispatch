package net.eric_nicolas.jmdispatch;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestFunctions2 {

    static PrintStream out;

    @Dispatch
    public static void f_A_X(A a, X x) {
        out.println("A=" + a.a + " X=" + x.x);
    }

    @Dispatch
    public static void f_B_Y(B b, Y y) {
        out.println("B=" + b.a + "," + b.b + " Y=" + y.x + "," + y.y);
    }

    private static final DispatchTable2 f_table = new DispatchTable2().autoregister(TestFunctions2.class);

    static void f(A a, X x) {
        f_table.dispatch(a, x);
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
        String expected = """
                A=1 X=4
                A=2 X=4
                A=1 X=5
                B=2,3 Y=5,6
                """;
        TestUtils.assertEquals(expected, result);
    }
}
