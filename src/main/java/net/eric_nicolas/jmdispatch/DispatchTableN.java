package net.eric_nicolas.jmdispatch;

/**
 * Dispatch table for N-argument multiple dispatch (N &ge; 2).
 *
 * <p>Usage:
 * <pre>{@code
 * DispatchTableN table = new DispatchTableN(3)
 *     .autoregister(MyHandlers.class);    // static handlers
 *     // or .autoregister(new MyHandlers());  // instance handlers
 *
 * Object result = table.dispatch(arg1, arg2, arg3);
 * }</pre>
 *
 * <p>The table discovers all {@link Dispatch @Dispatch}-annotated methods whose parameter
 * count matches the arity specified at construction. At dispatch time, it selects the
 * best-matching handler based on the runtime types of all arguments.
 *
 * <p>For 2-argument dispatch, prefer {@link DispatchTable2} which avoids varargs array
 * allocation and has a slightly tighter exact-match loop.
 *
 * <p>This class is thread-safe: the internal cache is updated atomically via
 * synchronized array append, and the lookup loop reads a snapshot of the array reference.
 *
 * @see DispatchTable2
 * @see Dispatch
 */
public class DispatchTableN extends DispatchTableAbstract<FunctorN> {

    /**
     * Creates a new empty N-argument dispatch table.
     *
     * @param nTypes the number of dispatch arguments (must be &ge; 2)
     * @throws IllegalArgumentException if {@code nTypes < 2}
     */
    public DispatchTableN(int nTypes) {
        super(nTypes, FunctorN.class, new FunctorImplementationBuilderN(nTypes));
        if (nTypes < 2) throw new IllegalArgumentException("DispatchTableN requires nTypes >= 2, got " + nTypes);
    }

    /**
     * Registers all static {@link Dispatch @Dispatch}-annotated methods from the given class.
     *
     * <p>Each annotated method must have exactly {@code nTypes} parameters with concrete
     * class types. Instance methods in the class are rejected with
     * {@link InvalidDispatchException}.
     *
     * @param aclass the class containing static {@code @Dispatch} methods
     * @return this table, for chaining
     * @throws InvalidDispatchException if any annotated method is invalid
     */
    public DispatchTableN autoregister(Class<?> aclass) {
        return (DispatchTableN) super.autoregister(aclass);
    }

    /**
     * Registers all {@link Dispatch @Dispatch}-annotated methods from the given instance.
     *
     * <p>Instance methods are bound to the provided object. Static methods in the same
     * class are also registered (without needing the instance). Each annotated method
     * must have exactly {@code nTypes} parameters with concrete class types.
     *
     * @param instance the object whose class is scanned and whose instance methods are bound
     * @return this table, for chaining
     * @throws InvalidDispatchException if any annotated method is invalid
     */
    public DispatchTableN autoregister(Object instance) {
        return (DispatchTableN) super.autoregister(instance);
    }

    /**
     * Dispatches to the best-matching handler for the runtime types of all arguments.
     *
     * <p>First attempts an exact match (identity comparison on argument classes). If no
     * exact match exists, computes inheritance distance to all registered handlers and
     * selects the closest match, caching the result for future calls.
     *
     * @param values the dispatch arguments (must not be null, count must equal {@code nTypes})
     * @return the handler's return value (boxed), or {@code null} for void handlers
     * @throws DispatchNoMatchException if no handler matches the argument types, or if
     *         any argument is null
     * @throws DispatchAmbiguousException if multiple handlers match with equal distance
     * @throws IllegalArgumentException if the number of arguments does not match {@code nTypes}
     */
    public Object dispatch(Object... values) {
        if (values.length != nTypes)
            throw new IllegalArgumentException("Expected " + nTypes + " arguments, got " + values.length);

        // null arguments cannot be dispatched
        for (int i = 0; i < values.length; ++i) {
            if (values[i] == null) {
                throw new DispatchNoMatchException("Cannot dispatch on null argument at position " + i);
            }
        }

        // search for exact match, pass-in values to delay the (costly) creation of Class<?>[] types
        FunctorN method = findExact(values);
        if (method == null) {
            // no direct match => search for the closest match
            Class<?>[] types = fromValues(values);
            method = findClosest(keys, functors, types); // throws is not found or ambiguous

            // add to the table for further lookups
            append(types, method);
        }

        // call the found method
        return method.f(values);
    }

    private FunctorN findExact(Object[] values) {
        Class<?> type0 = values[0].getClass();
        Class<?> type1 = values[1].getClass();
        outer:
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i][0] != type0) continue;
            if (keys[i][1] != type1) continue;
            for (int t = 2; t < nTypes; ++t) {
                if (keys[i][t] != values[t].getClass()) continue outer;
            }
            return functors[i];
        }
        return null;
    }
}
