#!/usr/bin/env bash
#
# bench.sh — compile and run JMDispatch microbenchmarks.
# Each scenario runs in its own JVM to avoid profile pollution.
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# ── Build ────────────────────────────────────────────────────────────────
echo "=== Building project ==="
mvn -q compile test-compile

# Resolve classpath via Maven
CP=$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout)
CP="target/classes:target/test-classes:$CP"

# Compile bench sources
echo "=== Compiling benchmarks ==="
javac -cp "$CP" -d target/bench-classes \
    src/bench/java/net/eric_nicolas/jmdispatch/DispatchBenchmark.java

CP="target/bench-classes:$CP"

# ── JVM flags ────────────────────────────────────────────────────────────
JVM_OPTS="-XX:+UseSerialGC -Xms512m -Xmx512m"

# ── Run scenarios ────────────────────────────────────────────────────────
SCENARIOS="exactHit2 exactHitN coldFallback2 coldFallbackN scaling"

echo ""
echo "============================================================"
echo "  JMDispatch Microbenchmark"
echo "============================================================"
echo ""

for scenario in $SCENARIOS; do
    java $JVM_OPTS -cp "$CP" net.eric_nicolas.jmdispatch.DispatchBenchmark "$scenario"
    echo ""
done

echo "=== Done ==="
