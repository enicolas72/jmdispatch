package net.eric_nicolas.jmdispatch.api_tests;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable;
import net.eric_nicolas.jmdispatch.TestUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Tests that dispatch works correctly with boolean, byte, and short primitive parameter types.
 */
public class TestMissingPrimitives {

    static PrintStream out;

    public static class Holder {
        String name;
        Holder(String name) { this.name = name; }
    }

    @Dispatch
    public static void withBoolean(boolean flag, Holder h) {
        out.println("boolean=" + flag + " " + h.name);
    }

    @Dispatch
    public static void withByte(byte b, Holder h) {
        out.println("byte=" + b + " " + h.name);
    }

    @Dispatch
    public static void withShort(short s, Holder h) {
        out.println("short=" + s + " " + h.name);
    }

    private static final DispatchTable table = DispatchTable.factory(2).autoregister(TestMissingPrimitives.class);

    @Test
    public void testBooleanDispatch() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);
        table.dispatch(true, new Holder("yes"));
        out.close();
        TestUtils.assertEquals("boolean=true yes\n", baos.toString());
    }

    @Test
    public void testByteDispatch() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);
        table.dispatch((byte) 42, new Holder("val"));
        out.close();
        TestUtils.assertEquals("byte=42 val\n", baos.toString());
    }

    @Test
    public void testShortDispatch() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);
        table.dispatch((short) 100, new Holder("num"));
        out.close();
        TestUtils.assertEquals("short=100 num\n", baos.toString());
    }
}
