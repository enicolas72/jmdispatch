package net.eric_nicolas.jmdispatch;

import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestFunctions2Native {

    static PrintStream out;

    @Dispatch
    public static void f_i_A(int i, A a) {
        out.println("i=" + i + " A=" + a.a);
    }

    @Dispatch
    public static void f_a_B(double d, B b) {
        out.println("d=" + d + " B=" + b.a + "," + b.b);
    }

    private static final DispatchTable2 f_table = new DispatchTable2().autoregister(TestFunctions2Native.class);

    static void f(Object o1, Object o2) {
        f_table.dispatch(o1, o2);
    }

    @Test
    public void test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);
        A a = new A(1);
        A b = new B(2, 3);
        B c = new C(4, 5, 6);
        f(1, a); //dispatch to f_1_X
        f(2.0, b); // dispatch to f_d_B
        f(2.0, c); // fallback dispatch to f_d_B
        f(1, b); // fallback dispatch to f_i_A
        out.close();
        String result = baos.toString();
        String expected = "i=1 A=1\nd=2.0 B=2,3\nd=2.0 B=4,5\ni=1 A=2\n";
        TestUtils.assertEquals(expected, result);
    }
}
