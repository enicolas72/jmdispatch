# Thoughts on JMDispatch

## What's genuinely good

- **It solves a real problem.** Java's lack of multiple dispatch is a well-known pain point. The visitor pattern is the usual workaround and it's verbose and brittle. This is cleaner.
- **The dispatch algorithm is solid.** Euclidean norm on inheritance distance is a smart heuristic — it naturally favors specific matches and the ambiguity detection via sorted-distance equivalence is elegant.
- **ASM bytecode generation is the right call.** Avoiding reflection at dispatch time is important if this sits in a hot path. The generated functors are essentially zero-overhead wrappers.
- **The caching strategy (appending resolved dispatches back into the table) is simple and effective** — first call pays the cost, subsequent calls hit the fast exact-match path.
- **The linear scan in `findExact()` is actually optimal for typical handler counts.** Benchmarking showed that replacing it with a `HashMap` (including zero-allocation variants using `ConcurrentHashMap` + `ThreadLocal` probe keys + identity hash codes) made exact-hit dispatch 4-7x slower (2.2 → 8.8–14.2 ns/op for 2 handlers). The pointer-comparison loop (`keys[i][0] == type1`) is so tight that even at 20 handlers (6.8 ns) it beats the HashMap's constant overhead. The crossover point would be ~50+ handlers, which is far beyond typical use.

## Design decisions

- **No interface or abstract parameter types.** Dispatch operates on concrete class hierarchies only. The inheritance distance is computed by walking the superclass chain (`getSuperclass()`), which is well-defined and unambiguous for concrete classes — each class has exactly one superclass path to `Object`. Interfaces would introduce multiple inheritance paths (diamond problem), making distance computation ambiguous and order-dependent. Since you always dispatch on *actual objects* (which are always instances of concrete classes), restricting handler parameter types to concrete classes is both simpler and correct. Registration rejects interface and abstract parameter types at autoregister time.

## Overall

It's a well-crafted proof of concept / personal library. The core algorithm is sound and the ASM plumbing is competent. As a focused tool for event handling, message routing, or similar dispatch-heavy patterns — it's perfectly usable as-is.
