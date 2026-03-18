package net.eric_nicolas.jmdispatch.api_tests.args3;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable;
import net.eric_nicolas.jmdispatch.TestUtils;
import net.eric_nicolas.jmdispatch.TestUtils.*;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestNativeArguments {

    static PrintStream out;

    @Dispatch
    public static void f_i_l_A(int i, Long l, A a) {
        out.println("i=" + i + " l=" + l + " A=" + a.a);
    }

    @Dispatch
    public static void f_f_d_B(float f, double d, B b) {
        out.println("f=" + f + " d=" + d + " B=" + b.a + "," + b.b);
    }

    private static final DispatchTable table = DispatchTable.factory(3).autoregister(TestNativeArguments.class);

    static void f(Object o1, Object o2, Object o3) {
        table.dispatch(o1, o2, o3);
    }

    @Test
    public void test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);
        A a = new A(1);
        A b = new B(2, 3);
        B c = new C(4, 5, 6);

        f(1, 5L, a); //l dispatch to f_i_l_A
        f(2.0F, 3.0, b); // dispatch to f_f_d_B
        f(2.0f, 3.0, c); // fallback dispatch to f_f_d_B
        f(1, 5L, b); // fallback dispatch to f_i_l_A

        out.close();
        String result = baos.toString();
        String expected = "i=1 l=5 A=1\nf=2.0 d=3.0 B=2,3\nf=2.0 d=3.0 B=4,5\ni=1 l=5 A=2\n";
        TestUtils.assertEquals(expected, result);
    }
}
