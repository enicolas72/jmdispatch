package net.eric_nicolas.jmdispatch;

import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestRegistrationValidation {

    // ── Interface parameter type ─────────────────────────────────────────

    public interface Printable {}

    public static class InterfaceParamHandler {
        @Dispatch
        public static void handle(Printable p, A a) {}
    }

    @Test(expected = InvalidDispatchException.class)
    public void testInterfaceParameterTypeRejected() {
        new DispatchTable2().autoregister(InterfaceParamHandler.class);
    }

    public static class InterfaceSecondParamHandler {
        @Dispatch
        public static void handle(A a, Printable p) {}
    }

    @Test(expected = InvalidDispatchException.class)
    public void testInterfaceSecondParameterTypeRejected() {
        new DispatchTable2().autoregister(InterfaceSecondParamHandler.class);
    }

    // ── Abstract parameter type ──────────────────────────────────────────

    public static abstract class AbstractShape {
        int sides;
    }

    public static class AbstractParamHandler {
        @Dispatch
        public static void handle(AbstractShape s, A a) {}
    }

    @Test(expected = InvalidDispatchException.class)
    public void testAbstractParameterTypeRejected() {
        new DispatchTable2().autoregister(AbstractParamHandler.class);
    }

    // ── Abstract @Dispatch method ────────────────────────────────────────

    public static abstract class AbstractMethodHandler {
        @Dispatch
        public abstract void handle(A a, X x);
    }

    @Test(expected = InvalidDispatchException.class)
    public void testAbstractDispatchMethodRejected() {
        // Can't instantiate abstract class, but autoregister(Class) should reject
        // the abstract method before getting that far
        new DispatchTable2().autoregister(AbstractMethodHandler.class);
    }

    // ── N-arg validation ─────────────────────────────────────────────────

    public static class InterfaceParamHandlerN {
        @Dispatch
        public static void handle(A a, Printable p, X x) {}
    }

    @Test(expected = InvalidDispatchException.class)
    public void testInterfaceParameterTypeRejectedN() {
        new DispatchTableN(3).autoregister(InterfaceParamHandlerN.class);
    }

    // ── Concrete classes still work fine ──────────────────────────────────

    public static class ValidHandler {
        @Dispatch
        public static String handle(A a, X x) { return "OK"; }
    }

    @Test
    public void testConcreteParameterTypesAccepted() {
        DispatchTable2 table = new DispatchTable2().autoregister(ValidHandler.class);
        Object result = table.dispatch(new A(1), new X(2));
        org.junit.Assert.assertEquals("OK", result);
    }

    @Test
    public void testConcreteSubclassParameterTypesAccepted() {
        // B extends A, Y extends X — both concrete
        DispatchTable2 table = new DispatchTable2().autoregister(ValidHandler.class);
        Object result = table.dispatch(new B(1, 2), new Y(3, 4));
        org.junit.Assert.assertEquals("OK", result);
    }
}
