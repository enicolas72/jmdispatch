package net.eric_nicolas.jmdispatch;

import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestFunctions3 {

    static PrintStream out;

    @Dispatch
    public static void f_A_B_X(A a, B b, X x) {
        out.println("A=" + a.a + " B=" + b.a + "," + b.b + " X=" + x.x);
    }

    @Dispatch
    public static void f_A_C_Y(A a, C c, Y y) {
        out.println("A=" + a.a + " C=" + c.a + "," + c.b + "," + c.c + " Y=" + y.x + "," + y.y);
    }

    private static final DispatchTableN f_table = new DispatchTableN(3).autoregister(TestFunctions3.class);

    static void f(A a, B b, X x) {
        f_table.dispatch(a, b, x);
    }

    @Test
    public void test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);

        A a = new A(1);
        B b = new B(2, 3);
        B c = new C(4, 5, 6);
        X x = new X(7);
        X y = new Y(8, 9);

        f(a, b, x); // dispatch to f_A_B_X
        f(a, c, y); // dispatch to f_A_C_Y
        f(a, b, y); // fallback dispatch to f_A_B_X
        f(a, c, x); // fallback dispatch to f_A_B_X

        out.close();
        String result = baos.toString();
        String expected = """
                A=1 B=2,3 X=7
                A=1 C=4,5,6 Y=8,9
                A=1 B=2,3 X=8
                A=1 B=4,5 X=7
                """;
        TestUtils.assertEquals(expected, result);
    }
}
