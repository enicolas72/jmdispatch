# Thoughts on JMDispatch

## What's genuinely good

- **It solves a real problem.** Java's lack of multiple dispatch is a well-known pain point. The visitor pattern is the usual workaround and it's verbose and brittle. This is cleaner.
- **The dispatch algorithm is solid.** Euclidean norm on inheritance distance is a smart heuristic — it naturally favors specific matches and the ambiguity detection via sorted-distance equivalence is elegant.
- **ASM bytecode generation is the right call.** Avoiding reflection at dispatch time is important if this sits in a hot path. The generated functors are essentially zero-overhead wrappers.
- **The caching strategy (appending resolved dispatches back into the table) is simple and effective** — first call pays the cost, subsequent calls hit the fast exact-match path.

## What could be improved

- **Static methods only.** This is the biggest limitation. Real-world use cases often want to dispatch on instance methods, or at least have access to instance state. The `@Dispatch` + static method pattern feels like it fights against Java's grain rather than working with it. You end up passing state through parameters or static fields (as the tests do with `static PrintStream out`).
- **The linear scan in `findExact()`** won't scale if you register many handlers. A `HashMap<List<Class<?>>, FUNCTOR>` would give O(1) exact lookups instead of O(n). For 5-10 handlers it doesn't matter, but it's worth noting.
- **Interface distance is approximate.** The current approach walks `getSuperclass()` and checks when `isAssignableFrom` stops — but this doesn't account for the diamond problem or multiple interface inheritance paths. For example, if `class Foo implements A, B` and both `A` and `B` extend `C`, the distance to `C` depends on which path you'd take. It works for simple hierarchies but could surprise users with complex ones.

## Overall

It's a well-crafted proof of concept / personal library. The core algorithm is sound and the ASM plumbing is competent. For it to be a "real" library that others adopt, it would need return value support, thread safety, and instance method dispatch. But as a focused tool for your own projects — say, event handling or message routing — it's perfectly usable as-is.
