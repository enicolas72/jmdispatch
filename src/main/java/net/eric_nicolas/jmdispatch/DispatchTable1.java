package net.eric_nicolas.jmdispatch;

/**
 * Dispatch table optimized for 1-argument multiple dispatch.
 *
 * <p>Usage:
 * <pre>{@code
 * DispatchTable1 table = new DispatchTable1()
 *     .autoregister(MyHandlers.class);    // static handlers
 *     // or .autoregister(new MyHandlers());  // instance handlers
 *
 * Object result = table.dispatch(arg1);
 * }</pre>
 *
 * <p>The table discovers all {@link Dispatch @Dispatch}-annotated methods with exactly
 * 1 parameter in the registered class (or instance). At dispatch time, it selects the
 * best-matching handler based on the runtime type of the argument.
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
public class DispatchTable1 extends DispatchTableAbstract<Functor1> {

    /**
     * Creates a new empty 1-argument dispatch table.
     */
    public DispatchTable1() {
        super(1, Functor1.class, new FunctorImplementationBuilderDirect(Functor1.class, 1));
    }

    /**
     * Registers all static {@link Dispatch @Dispatch}-annotated methods from the given class.
     *
     * <p>Each annotated method must have exactly 1 parameter with concrete class types.
     * Instance methods in the class are rejected with {@link InvalidDispatchException}.
     *
     * @param aclass the class containing static {@code @Dispatch} methods
     * @return this table, for chaining
     * @throws InvalidDispatchException if any annotated method is invalid
     */
    public DispatchTable1 autoregister(Class<?> aclass) {
        return (DispatchTable1) super.autoregister(aclass);
    }

    /**
     * Registers all {@link Dispatch @Dispatch}-annotated methods from the given instance.
     *
     * <p>Instance methods are bound to the provided object. Static methods in the same
     * class are also registered (without needing the instance). Each annotated method
     * must have exactly 1 parameter with concrete class types.
     *
     * @param instance the object whose class is scanned and whose instance methods are bound
     * @return this table, for chaining
     * @throws InvalidDispatchException if any annotated method is invalid
     */
    public DispatchTable1 autoregister(Object instance) {
        return (DispatchTable1) super.autoregister(instance);
    }

    /**
     * Dispatches to the best-matching handler for the runtime type of the argument.
     *
     * <p>First attempts an exact match (identity comparison on argument classes). If no
     * exact match exists, computes inheritance distance to all registered handlers and
     * selects the closest match, caching the result for future calls.
     *
     * @param v the argument (must not be null)
     * @return the handler's return value (boxed), or {@code null} for void handlers
     * @throws DispatchNoMatchException if no handler matches the argument type, or if
     *         the argument is null
     * @throws DispatchAmbiguousException if multiple handlers match with equal distance
     */
    public Object dispatch(Object v) {
        // null arguments cannot be dispatched
        if (v == null) {
            throw new DispatchNoMatchException("Cannot dispatch on null argument(s):  (null) ");
        }

        // search for exact match, pass-in individual types to delay the (costly) creation of Class<?>[] types
        Functor1 method = findExact(v.getClass());
        if (method == null) {
            // no direct match => search for the closest match
            Class<?>[] types = new Class<?>[]{v.getClass()};
            method = findClosest(keys, functors, types); // throws if not found or ambiguous

            // add to the table for further Lookups
            append(types, method);
        }

        // call the found method
        return method.f(v);
    }

    private Functor1 findExact(Class<?> type1) {
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i][0] == type1) {
                return functors[i];
            }
        }
        return null;
    }
}