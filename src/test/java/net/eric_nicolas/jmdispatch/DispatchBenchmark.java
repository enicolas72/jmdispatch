package net.eric_nicolas.jmdispatch;

import static net.eric_nicolas.jmdispatch.TestUtils.*;

/**
 * Hand-rolled microbenchmark for JMDispatch.
 *
 * Run all scenarios from main(). Each scenario is self-contained within
 * a single JVM — profile pollution between scenarios is minimal since
 * the JIT compiles each dispatch table independently.
 *
 * Recommended JVM flags: -XX:+UseSerialGC -Xms512m -Xmx512m
 *
 * From IntelliJ: right-click and "Run DispatchBenchmark.main()"
 * From command line: mvn -q test-compile && java -XX:+UseSerialGC -Xms512m -Xmx512m \
 *   -cp $(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout):target/classes:target/test-classes \
 *   net.eric_nicolas.jmdispatch.DispatchBenchmark
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

    // 2-handler classes for DispatchTable
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

    static void benchExactHit2(DispatchTable table) {
        A a = new A(1);
        X x = new X(2);
        for (int i = 0; i < OPS_PER_ITERATION; i++) {
            sink = table.dispatch(a, x);
        }
    }

    static void benchExactHitN(DispatchTable table) {
        A a = new A(1);
        X x = new X(2);
        A a2 = new A(3);
        for (int i = 0; i < OPS_PER_ITERATION; i++) {
            sink = table.dispatch(a, x, a2);
        }
    }

    static void benchColdFallback2(DispatchTable[] tables) {
        // Each table is fresh — dispatching B,X triggers findClosest (B is subtype of A)
        B b = new B(1, 2);
        X x = new X(3);
        for (int i = 0; i < tables.length; i++) {
            sink = tables[i].dispatch(b, x);
        }
    }

    static void benchColdFallbackN(DispatchTable[] tables) {
        B b = new B(1, 2);
        X x = new X(3);
        B b2 = new B(4, 5);
        for (int i = 0; i < tables.length; i++) {
            sink = tables[i].dispatch(b, x, b2);
        }
    }

    static void benchScaling2(DispatchTable table) {
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

    static DispatchTable[] makeColdTables2(int count) {
        DispatchTable[] tables = new DispatchTable[count];
        for (int i = 0; i < count; i++) {
            tables[i] = DispatchTable.factory(2);
            tables[i].autoregister(Handlers2_Size2.class);
        }
        return tables;
    }

    static DispatchTable[] makeColdTablesN(int count) {
        DispatchTable[] tables = new DispatchTable[count];
        for (int i = 0; i < count; i++) {
            tables[i] = DispatchTable.factory(3);
            tables[i].autoregister(HandlersN_Size2.class);
        }
        return tables;
    }

    // ── Scenarios ────────────────────────────────────────────────────────

    static void scenario_exactHit2() {
        DispatchTable table = DispatchTable.factory(2);
        table.autoregister(Handlers2_Size2.class);
        table.dispatch(new A(0), new X(0));
        measure("exactHit2arg", null, () -> benchExactHit2(table));
    }

    static void scenario_exactHitN() {
        DispatchTable table = DispatchTable.factory(3);
        table.autoregister(HandlersN_Size2.class);
        table.dispatch(new A(0), new X(0), new A(0));
        measure("exactHitN(3)arg", null, () -> benchExactHitN(table));
    }

    static void scenario_coldFallback2() {
        final DispatchTable[][] holder = new DispatchTable[1][];
        measure("coldFallback2arg", COLD_OPS_PER_ITERATION,
                () -> holder[0] = makeColdTables2(COLD_OPS_PER_ITERATION),
                () -> benchColdFallback2(holder[0]));
    }

    static void scenario_coldFallbackN() {
        final DispatchTable[][] holder = new DispatchTable[1][];
        measure("coldFallbackN(3)arg", COLD_OPS_PER_ITERATION,
                () -> holder[0] = makeColdTablesN(COLD_OPS_PER_ITERATION),
                () -> benchColdFallbackN(holder[0]));
    }

    static void scenario_scaling() {
        DispatchTable t2 = DispatchTable.factory(2);
        t2.autoregister(Handlers2_Size2.class);
        t2.dispatch(new A(0), new X(0));
        measure("scaling_2handlers", null, () -> benchScaling2(t2));

        DispatchTable t5 = DispatchTable.factory(2);
        t5.autoregister(Handlers2_Size5.class);
        t5.dispatch(new A(0), new X(0));
        measure("scaling_5handlers", null, () -> benchScaling2(t5));

        DispatchTable t10 = DispatchTable.factory(2);
        t10.autoregister(Handlers2_Size10.class);
        t10.dispatch(new A(0), new X(0));
        measure("scaling_10handlers", null, () -> benchScaling2(t10));

        DispatchTable t20 = DispatchTable.factory(2);
        t20.autoregister(Handlers2_Size20.class);
        t20.dispatch(new A(0), new X(0));
        measure("scaling_20handlers", null, () -> benchScaling2(t20));
    }

    // ── Main ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  JMDispatch Microbenchmark");
        System.out.println("  Warmup: " + WARMUP_ITERATIONS + " iterations, Measurement: " + MEASUREMENT_ITERATIONS + " iterations");
        System.out.println("============================================================");
        System.out.println();

        System.out.println("--- Exact hit (warm cache) ---");
        scenario_exactHit2();
        scenario_exactHitN();
        System.out.println();

        System.out.println("--- Cold fallback (findClosest, " + COLD_OPS_PER_ITERATION + " ops/iter) ---");
        scenario_coldFallback2();
        scenario_coldFallbackN();
        System.out.println();

        System.out.println("--- Table size scaling (exact hit) ---");
        scenario_scaling();
        System.out.println();

        System.out.println("=== Done ===");
    }
}
