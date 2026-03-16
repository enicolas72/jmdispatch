package net.eric_nicolas.jmdispatch;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestFunctions1Native {

    static PrintStream out;

    @Dispatch
    public static void f_int(int i) {
        out.println("int=" + i);
    }

    @Dispatch
    public static void f_double(double d) {
        out.println("double=" + d);
    }

    @Dispatch
    public static void f_string(String s) {
        out.println("string=" + s);
    }

    private static final DispatchTable1 f_table = new DispatchTable1().autoregister(TestFunctions1Native.class);

    static void f(Object o) {
        f_table.dispatch(o);
    }

    @Test
    public void test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);
        f(1);        // autoboxed to Integer => dispatch to f_int
        f(2.0);      // autoboxed to Double => dispatch to f_double
        f("hello");  // dispatch to f_string
        out.close();
        String result = baos.toString();
        String expected = "int=1\ndouble=2.0\nstring=hello\n";
        TestUtils.assertEquals(expected, result);
    }
}
