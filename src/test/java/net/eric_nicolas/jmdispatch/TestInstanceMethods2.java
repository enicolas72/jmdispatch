package net.eric_nicolas.jmdispatch;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestInstanceMethods2 {

    // Handler with instance state
    public static class MyHandler {
        private final PrintStream out;

        MyHandler(PrintStream out) {
            this.out = out;
        }

        @Dispatch
        public void handle(A a, X x) {
            out.println("A=" + a.a + " X=" + x.x);
        }

        @Dispatch
        public void handle(B b, Y y) {
            out.println("B=" + b.a + "," + b.b + " Y=" + y.x + "," + y.y);
        }
    }

    @Test
    public void testInstanceDispatch() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);

        DispatchTable2 table = new DispatchTable2().autoregister(new MyHandler(out));

        table.dispatch(new A(1), new X(4));
        table.dispatch(new B(2, 3), new Y(5, 6));

        out.close();
        TestUtils.assertEquals("A=1 X=4\nB=2,3 Y=5,6", baos.toString());
    }

    @Test
    public void testInstanceDispatchFallback() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);

        DispatchTable2 table = new DispatchTable2().autoregister(new MyHandler(out));

        // B is subtype of A, X is exact => fallback to handle(A, X)
        table.dispatch(new B(2, 3), new X(4));

        out.close();
        TestUtils.assertEquals("A=2 X=4", baos.toString());
    }

    // Handler with return values
    public static class ReturningHandler {
        private final String prefix;

        ReturningHandler(String prefix) {
            this.prefix = prefix;
        }

        @Dispatch
        public String describe(A a, X x) {
            return prefix + "A=" + a.a + " X=" + x.x;
        }

        @Dispatch
        public String describe(B b, Y y) {
            return prefix + "B=" + b.a + "," + b.b + " Y=" + y.x + "," + y.y;
        }
    }

    @Test
    public void testInstanceReturnValues() {
        DispatchTable2 table = new DispatchTable2().autoregister(new ReturningHandler("[1]"));

        Object result1 = table.dispatch(new A(1), new X(4));
        org.junit.Assert.assertEquals("[1]A=1 X=4", result1);

        Object result2 = table.dispatch(new B(2, 3), new Y(5, 6));
        org.junit.Assert.assertEquals("[1]B=2,3 Y=5,6", result2);
    }

    @Test
    public void testTwoInstancesSameHandlerClass() {
        // Two different instances of the same handler class on separate tables
        DispatchTable2 table1 = new DispatchTable2().autoregister(new ReturningHandler("[1]"));
        DispatchTable2 table2 = new DispatchTable2().autoregister(new ReturningHandler("[2]"));

        Object r1 = table1.dispatch(new A(1), new X(2));
        Object r2 = table2.dispatch(new A(1), new X(2));

        org.junit.Assert.assertEquals("[1]A=1 X=2", r1);
        org.junit.Assert.assertEquals("[2]A=1 X=2", r2);
    }
}
