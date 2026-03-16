package net.eric_nicolas.jmdispatch;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestInstanceMethods1 {

    // Handler with instance state
    public static class MyHandler {
        private final PrintStream out;

        MyHandler(PrintStream out) {
            this.out = out;
        }

        @Dispatch
        public void handle(A a) {
            out.println("A=" + a.a);
        }

        @Dispatch
        public void handle(B b) {
            out.println("B=" + b.a + "," + b.b);
        }
    }

    @Test
    public void testInstanceDispatch() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);

        DispatchTable1 table = new DispatchTable1().autoregister(new MyHandler(out));

        table.dispatch(new A(1));
        table.dispatch(new B(2, 3));

        out.close();
        TestUtils.assertEquals("A=1\nB=2,3", baos.toString());
    }

    @Test
    public void testInstanceDispatchFallback() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);

        DispatchTable1 table = new DispatchTable1().autoregister(new MyHandler(out));

        // C is subtype of B => fallback to handle(B)
        table.dispatch(new C(4, 5, 6));

        out.close();
        TestUtils.assertEquals("B=4,5", baos.toString());
    }

    // Handler with return values
    public static class ReturningHandler {
        private final String prefix;

        ReturningHandler(String prefix) {
            this.prefix = prefix;
        }

        @Dispatch
        public String describe(A a) {
            return prefix + "A=" + a.a;
        }

        @Dispatch
        public String describe(B b) {
            return prefix + "B=" + b.a + "," + b.b;
        }
    }

    @Test
    public void testInstanceReturnValues() {
        DispatchTable1 table = new DispatchTable1().autoregister(new ReturningHandler("[1]"));

        Object result1 = table.dispatch(new A(1));
        org.junit.Assert.assertEquals("[1]A=1", result1);

        Object result2 = table.dispatch(new B(2, 3));
        org.junit.Assert.assertEquals("[1]B=2,3", result2);
    }

    @Test
    public void testTwoInstancesSameHandlerClass() {
        DispatchTable1 table1 = new DispatchTable1().autoregister(new ReturningHandler("[1]"));
        DispatchTable1 table2 = new DispatchTable1().autoregister(new ReturningHandler("[2]"));

        Object r1 = table1.dispatch(new A(1));
        Object r2 = table2.dispatch(new A(1));

        org.junit.Assert.assertEquals("[1]A=1", r1);
        org.junit.Assert.assertEquals("[2]A=1", r2);
    }
}
