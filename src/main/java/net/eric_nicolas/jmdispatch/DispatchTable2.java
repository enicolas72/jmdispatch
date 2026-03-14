package net.eric_nicolas.jmdispatch;

/**
 * Dispatch table optimized for 2-argument multiple dispatch.
 *
 * <p>Usage:
 * <pre>{@code
 * DispatchTable2 table = new DispatchTable2()
 *     .autoregister(MyHandlers.class);    // static handlers
 *     // or .autoregister(new MyHandlers());  // instance handlers
 *
 * Object result = table.dispatch(arg1, arg2);
 * }</pre>
 *
 * <p>The table discovers all {@link Dispatch @Dispatch}-annotated methods with exactly
 * 2 parameters in the registered class (or instance). At dispatch time, it selects the
 * best-matching handler based on the runtime types of both arguments.
 *
 * <p>Exact-match lookups use an optimized linear scan with identity comparison on
 * {@code Class} objects. On the first dispatch for a given type combination that has
 * no exact handler, the closest match is resolved via inheritance distance and cached
 * for subsequent calls.
 *
 * <p>This class is thread-safe: the internal cache is updated atomically via
 * synchronized array append, and the lookup loop reads a snapshot of the array reference.
 *
 * @see DispatchTableN
 * @see Dispatch
 */
public class DispatchTable2 extends DispatchTableAbstract<Functor2> {

    /**
     * Creates a new empty 2-argument dispatch table.
     */
    public DispatchTable2() {
        super(2, Functor2.class, new FunctorImplementationBuilder2());
    }

    /**
     * Registers all static {@link Dispatch @Dispatch}-annotated methods from the given class.
     *
     * <p>Each annotated method must have exactly 2 parameters with concrete class types.
     * Instance methods in the class are rejected with {@link InvalidDispatchException}.
     *
     * @param aclass the class containing static {@code @Dispatch} methods
     * @return this table, for chaining
     * @throws InvalidDispatchException if any annotated method is invalid
     */
    public DispatchTable2 autoregister(Class<?> aclass) {
        return (DispatchTable2) super.autoregister(aclass);
    }

    /**
     * Registers all {@link Dispatch @Dispatch}-annotated methods from the given instance.
     *
     * <p>Instance methods are bound to the provided object. Static methods in the same
     * class are also registered (without needing the instance). Each annotated method
     * must have exactly 2 parameters with concrete class types.
     *
     * @param instance the object whose class is scanned and whose instance methods are bound
     * @return this table, for chaining
     * @throws InvalidDispatchException if any annotated method is invalid
     */
    public DispatchTable2 autoregister(Object instance) {
        return (DispatchTable2) super.autoregister(instance);
    }

    /**
     * Dispatches to the best-matching handler for the runtime types of the two arguments.
     *
     * <p>First attempts an exact match (identity comparison on argument classes). If no
     * exact match exists, computes inheritance distance to all registered handlers and
     * selects the closest match, caching the result for future calls.
     *
     * @param v1 the first argument (must not be null)
     * @param v2 the second argument (must not be null)
     * @return the handler's return value (boxed), or {@code null} for void handlers
     * @throws DispatchNoMatchException if no handler matches the argument types, or if
     *         either argument is null
     * @throws DispatchAmbiguousException if multiple handlers match with equal distance
     */
    public Object dispatch(Object v1, Object v2) {
        // null arguments cannot be dispatched
        if (v1 == null || v2 == null) {
            String t1 = v1 == null ? "null" : v1.getClass().getCanonicalName();
            String t2 = v2 == null ? "null" : v2.getClass().getCanonicalName();
            throw new DispatchNoMatchException("Cannot dispatch on null argument(s):  (" + t1 + "," + t2 + ") ");
        }

        // search for exact match, pass-in individual types to delay the (costly) creation of Class<?>[] types
        Functor2 method = findExact(v1.getClass(), v2.getClass());
        if (method == null) {
            // no direct match => search for the closest match
            Class<?>[] types = new Class<?>[]{v1.getClass(), v2.getClass()};
            method = findClosest(keys, functors, types); // throws if not found or ambiguous

            // add to the table for further Lookups
            append(types, method);
        }

        // call the found method
        return method.f(v1, v2);
    }

    private Functor2 findExact(Class<?> type1, Class<?> type2) {
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i][0] == type1 && keys[i][1] == type2) {
                return functors[i];
            }
        }
        return null;
    }
}