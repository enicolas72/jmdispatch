# Thoughts on JMDispatch

## What's genuinely good

- **It solves a real problem.** Java's lack of multiple dispatch is a well-known pain point. The visitor pattern is the usual workaround and it's verbose and brittle. This is cleaner.
- **The dispatch algorithm is solid.** Euclidean norm on inheritance distance is a smart heuristic — it naturally favors specific matches and the ambiguity detection via sorted-distance equivalence is elegant.
- **ASM bytecode generation is the right call.** Avoiding reflection at dispatch time is important if this sits in a hot path. The generated functors are essentially zero-overhead wrappers.
- **The caching strategy (appending resolved dispatches back into the table) is simple and effective** — first call pays the cost, subsequent calls hit the fast exact-match path.
- **The linear scan in `findExact()` is actually optimal for typical handler counts.** Benchmarking showed that replacing it with a `HashMap` (including zero-allocation variants using `ConcurrentHashMap` + `ThreadLocal` probe keys + identity hash codes) made exact-hit dispatch 4-7x slower (2.2 → 8.8–14.2 ns/op for 2 handlers). The pointer-comparison loop (`keys[i][0] == type1`) is so tight that even at 20 handlers (6.8 ns) it beats the HashMap's constant overhead. The crossover point would be ~50+ handlers, which is far beyond typical use.

## Design decisions

- **No interface or abstract parameter types.** Dispatch operates on concrete class hierarchies only. The inheritance distance is computed by walking the superclass chain (`getSuperclass()`), which is well-defined and unambiguous for concrete classes — each class has exactly one superclass path to `Object`. Interfaces would introduce multiple inheritance paths (diamond problem), making distance computation ambiguous and order-dependent. Since you always dispatch on *actual objects* (which are always instances of concrete classes), restricting handler parameter types to concrete classes is both simpler and correct. Registration rejects interface and abstract parameter types at autoregister time.
- **Typed exceptions.** All exceptions are typed: `DispatchNoMatchException` and `DispatchAmbiguousException` for dispatch-time errors, `InvalidDispatchException` for registration-time validation errors (abstract methods, interface/abstract parameter types, duplicate signatures, wrong argument count). No raw `RuntimeException` in user-facing paths.

## Current state

This is a production-quality library with a tight scope. The core dispatch algorithm is sound, the ASM codegen is zero-overhead, and the hot path benchmarks at ~2-7 ns/op depending on table size. It supports static and instance handlers, return values (object, primitive, void), thread-safe caching, and validates everything at registration time with typed exceptions. 20 test files cover happy paths, error cases, and edge cases thoroughly.

What remains before publishing to Maven Central would be packaging concerns — javadoc on public API, CI, release versioning, distribution config — not quality concerns. The core is solid and ready for real use in event handling, message routing, or any dispatch-heavy pattern.
