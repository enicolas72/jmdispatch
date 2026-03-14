package net.eric_nicolas.jmdispatch;

import static net.eric_nicolas.jmdispatch.TestUtils.*;

/**
 * Hand-rolled microbenchmark for JMDispatch.
 *
 * Run via bench.sh which launches each scenario in a separate JVM.
 */
public class DispatchBenchmark {

    static final int WARMUP_ITERATIONS = 5;
    static final int MEASUREMENT_ITERATIONS = 10;
    static final int OPS_PER_ITERATION = 1_000_000;
    // Cold fallback creates a fresh table+classloader per op, so use fewer ops
    static final int COLD_OPS_PER_ITERATION = 10_000;

    static volatile Object sink;

    // ── Extra type hierarchy for scaling scenarios ────────────────────────
    // We need more than 6 type combos (A/B/C × X/Y) for 10+ handler tables.

    public static class D extends C { D() { super(0,0,0); } }
    public static class E extends D { E() { super(); } }
    public static class Z extends Y { Z() { super(0,0); } }
    public static class W extends Z { W() { super(); } }

    // ── Handler classes ──────────────────────────────────────────────────

    // 2-handler classes for DispatchTable2
    public static class Handlers2_Size2 {
        @Dispatch public static String handle(A a, X x) { return "AX"; }
        @Dispatch public static String handle(B b, Y y) { return "BY"; }
    }

    // 2-handler classes for DispatchTableN (3-arg)
    public static class HandlersN_Size2 {
        @Dispatch public static String handle(A a, X x, A a2) { return "AXA"; }
        @Dispatch public static String handle(B b, Y y, B b2) { return "BYB"; }
    }

    // Scaling: 5 handlers
    public static class Handlers2_Size5 {
        @Dispatch public static String handle(A a, X x) { return "AX"; }
        @Dispatch public static String handle(A a, Y y) { return "AY"; }
        @Dispatch public static String handle(B b, X x) { return "BX"; }
        @Dispatch public static String handle(B b, Y y) { return "BY"; }
        @Dispatch public static String handle(C c, X x) { return "CX"; }
    }

    // Scaling: 10 handlers — uses extended hierarchy D/E/Z/W
    public static class Handlers2_Size10 {
        @Dispatch public static String handle(A a, X x) { return "AX"; }
        @Dispatch public static String handle(A a, Y y) { return "AY"; }
        @Dispatch public static String handle(B b, X x) { return "BX"; }
        @Dispatch public static String handle(B b, Y y) { return "BY"; }
        @Dispatch public static String handle(C c, X x) { return "CX"; }
        @Dispatch public static String handle(C c, Y y) { return "CY"; }
        @Dispatch public static String handle(D d, X x) { return "DX"; }
        @Dispatch public static String handle(D d, Y y) { return "DY"; }
        @Dispatch public static String handle(E e, X x) { return "EX"; }
        @Dispatch public static String handle(E e, Y y) { return "EY"; }
    }

    // Scaling: 20 handlers — all combos of A/B/C/D/E × X/Y/Z/W
    public static class Handlers2_Size20 {
        @Dispatch public static String handle(A a, X x) { return "AX"; }
        @Dispatch public static String handle(A a, Y y) { return "AY"; }
        @Dispatch public static String handle(A a, Z z) { return "AZ"; }
        @Dispatch public static String handle(A a, W w) { return "AW"; }
        @Dispatch public static String handle(B b, X x) { return "BX"; }
        @Dispatch public static String handle(B b, Y y) { return "BY"; }
        @Dispatch public static String handle(B b, Z z) { return "BZ"; }
        @Dispatch public static String handle(B b, W w) { return "BW"; }
        @Dispatch public static String handle(C c, X x) { return "CX"; }
        @Dispatch public static String handle(C c, Y y) { return "CY"; }
        @Dispatch public static String handle(C c, Z z) { return "CZ"; }
        @Dispatch public static String handle(C c, W w) { return "CW"; }
        @Dispatch public static String handle(D d, X x) { return "DX"; }
        @Dispatch public static String handle(D d, Y y) { return "DY"; }
        @Dispatch public static String handle(D d, Z z) { return "DZ"; }
        @Dispatch public static String handle(D d, W w) { return "DW"; }
        @Dispatch public static String handle(E e, X x) { return "EX"; }
        @Dispatch public static String handle(E e, Y y) { return "EY"; }
        @Dispatch public static String handle(E e, Z z) { return "EZ"; }
        @Dispatch public static String handle(E e, W w) { return "EW"; }
    }

    // ── Benchmark methods ────────────────────────────────────────────────

    static void benchExactHit2(DispatchTable2 table) {
        A a = new A(1);
        X x = new X(2);
        for (int i = 0; i < OPS_PER_ITERATION; i++) {
            sink = table.dispatch(a, x);
        }
    }

    static void benchExactHitN(DispatchTableN table) {
        A a = new A(1);
        X x = new X(2);
        A a2 = new A(3);
        for (int i = 0; i < OPS_PER_ITERATION; i++) {
            sink = table.dispatch(a, x, a2);
        }
    }

    static void benchColdFallback2(DispatchTable2[] tables) {
        // Each table is fresh — dispatching B,X triggers findClosest (B is subtype of A)
        B b = new B(1, 2);
        X x = new X(3);
        for (int i = 0; i < tables.length; i++) {
            sink = tables[i].dispatch(b, x);
        }
    }

    static void benchColdFallbackN(DispatchTableN[] tables) {
        B b = new B(1, 2);
        X x = new X(3);
        B b2 = new B(4, 5);
        for (int i = 0; i < tables.length; i++) {
            sink = tables[i].dispatch(b, x, b2);
        }
    }

    static void benchScaling2(DispatchTable2 table) {
        // Dispatch exact hit on first registered handler
        A a = new A(1);
        X x = new X(2);
        for (int i = 0; i < OPS_PER_ITERATION; i++) {
            sink = table.dispatch(a, x);
        }
    }

    // ── Harness ──────────────────────────────────────────────────────────

    @FunctionalInterface
    interface BenchRunnable {
        void run();
    }

    static void measure(String name, BenchRunnable setup, BenchRunnable body) {
        measure(name, OPS_PER_ITERATION, setup, body);
    }

    static void measure(String name, int opsPerIteration, BenchRunnable setup, BenchRunnable body) {
        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            if (setup != null) setup.run();
            body.run();
        }

        // Measure
        double[] nsPerOp = new double[MEASUREMENT_ITERATIONS];
        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            if (setup != null) setup.run();
            long start = System.nanoTime();
            body.run();
            long elapsed = System.nanoTime() - start;
            nsPerOp[i] = (double) elapsed / opsPerIteration;
        }

        // Stats
        double sum = 0;
        for (double v : nsPerOp) sum += v;
        double mean = sum / nsPerOp.length;

        double sumSq = 0;
        for (double v : nsPerOp) sumSq += (v - mean) * (v - mean);
        double stddev = Math.sqrt(sumSq / nsPerOp.length);

        System.out.printf("%-45s %8.2f ± %6.2f ns/op%n", name, mean, stddev);
    }

    // ── Cold table factories ─────────────────────────────────────────────

    static DispatchTable2[] makeColdTables2(int count) {
        DispatchTable2[] tables = new DispatchTable2[count];
        for (int i = 0; i < count; i++) {
            tables[i] = new DispatchTable2();
            tables[i].autoregister(Handlers2_Size2.class);
        }
        return tables;
    }

    static DispatchTableN[] makeColdTablesN(int count) {
        DispatchTableN[] tables = new DispatchTableN[count];
        for (int i = 0; i < count; i++) {
            tables[i] = new DispatchTableN(3);
            tables[i].autoregister(HandlersN_Size2.class);
        }
        return tables;
    }

    // ── Scenarios ────────────────────────────────────────────────────────

    static void scenario1_exactHit2() {
        DispatchTable2 table = new DispatchTable2();
        table.autoregister(Handlers2_Size2.class);
        table.dispatch(new A(0), new X(0));
        measure("exactHit2arg", null, () -> benchExactHit2(table));
    }

    static void scenario2_exactHitN() {
        DispatchTableN table = new DispatchTableN(3);
        table.autoregister(HandlersN_Size2.class);
        table.dispatch(new A(0), new X(0), new A(0));
        measure("exactHitN(3)arg", null, () -> benchExactHitN(table));
    }

    static void scenario3_coldFallback2() {
        final DispatchTable2[][] holder = new DispatchTable2[1][];
        measure("coldFallback2arg", COLD_OPS_PER_ITERATION,
                () -> holder[0] = makeColdTables2(COLD_OPS_PER_ITERATION),
                () -> benchColdFallback2(holder[0]));
    }

    static void scenario4_coldFallbackN() {
        final DispatchTableN[][] holder = new DispatchTableN[1][];
        measure("coldFallbackN(3)arg", COLD_OPS_PER_ITERATION,
                () -> holder[0] = makeColdTablesN(COLD_OPS_PER_ITERATION),
                () -> benchColdFallbackN(holder[0]));
    }

    static void scenario5_scaling() {
        // 2 handlers
        DispatchTable2 t2 = new DispatchTable2();
        t2.autoregister(Handlers2_Size2.class);
        t2.dispatch(new A(0), new X(0));
        measure("scaling_2handlers", null, () -> benchScaling2(t2));

        // 5 handlers
        DispatchTable2 t5 = new DispatchTable2();
        t5.autoregister(Handlers2_Size5.class);
        t5.dispatch(new A(0), new X(0));
        measure("scaling_5handlers", null, () -> benchScaling2(t5));

        // 10 handlers
        DispatchTable2 t10 = new DispatchTable2();
        t10.autoregister(Handlers2_Size10.class);
        t10.dispatch(new A(0), new X(0));
        measure("scaling_10handlers", null, () -> benchScaling2(t10));

        // 20 handlers
        DispatchTable2 t20 = new DispatchTable2();
        t20.autoregister(Handlers2_Size20.class);
        t20.dispatch(new A(0), new X(0));
        measure("scaling_20handlers", null, () -> benchScaling2(t20));
    }

    // ── Main ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: DispatchBenchmark <scenario>");
            System.err.println("Scenarios: exactHit2, exactHitN, coldFallback2, coldFallbackN, scaling");
            System.exit(1);
        }

        String scenario = args[0];
        boolean cold = scenario.startsWith("coldFallback");
        int ops = cold ? COLD_OPS_PER_ITERATION : OPS_PER_ITERATION;
        System.out.println("# Scenario: " + scenario);
        System.out.println("# Warmup: " + WARMUP_ITERATIONS + " x " + ops + " ops");
        System.out.println("# Measurement: " + MEASUREMENT_ITERATIONS + " x " + ops + " ops");
        System.out.println();

        switch (scenario) {
            case "exactHit2":      scenario1_exactHit2(); break;
            case "exactHitN":      scenario2_exactHitN(); break;
            case "coldFallback2":  scenario3_coldFallback2(); break;
            case "coldFallbackN":  scenario4_coldFallbackN(); break;
            case "scaling":        scenario5_scaling(); break;
            default:
                System.err.println("Unknown scenario: " + scenario);
                System.exit(1);
        }
    }
}
