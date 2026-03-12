package net.eric_nicolas.jmdispatch;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Bug: distance() walks getSuperclass() only, so dispatching on interface types
 * causes an NPE when the superclass chain reaches Object without finding the interface.
 */
public class TestInterfaceDispatch {

    static PrintStream out;

    public interface Printable {}
    public interface Loggable {}

    public static class Doc implements Printable {
        String name;
        Doc(String name) { this.name = name; }
    }

    public static class Report implements Printable, Loggable {
        String title;
        Report(String title) { this.title = title; }
    }

    @Dispatch
    public static void handle(Printable p, Doc d) {
        out.println("Printable+Doc=" + d.name);
    }

    @Dispatch
    public static void handle(Loggable l, Report r) {
        out.println("Loggable+Report=" + r.title);
    }

    private static final DispatchTable2 table = new DispatchTable2().autoregister(TestInterfaceDispatch.class);

    @Test
    public void testInterfaceFirstArg() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);

        Doc doc = new Doc("readme");
        table.dispatch(doc, doc);

        out.close();
        String result = baos.toString();
        String expected = "Printable+Doc=readme\n";
        TestUtils.assertEquals(expected, result);
    }

    @Test
    public void testInterfaceSecondArg() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        out = new PrintStream(baos);

        Report report = new Report("Q1");
        table.dispatch(report, report);

        out.close();
        String result = baos.toString();
        String expected = "Loggable+Report=Q1\n";
        TestUtils.assertEquals(expected, result);
    }
}
