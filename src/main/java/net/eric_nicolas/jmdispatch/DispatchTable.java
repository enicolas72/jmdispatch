package net.eric_nicolas.jmdispatch;

import net.eric_nicolas.jmdispatch.impl.DispatchTableFactory;

/**
 * Dispatch table for N-argument multiple dispatch
 *
 * <p>Usage:
 * <pre>{@code
 * DispatchTable table = DispatchTable.factory(3)
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
 * <p>This class is thread-safe: the internal cache is updated atomically via
 * synchronized array append, and the lookup loop reads a snapshot of the array reference.
 *
 * @see Dispatch
 */
public interface DispatchTable {

    static DispatchTable factory(int nTypes) { return DispatchTableFactory.build(nTypes); }

    /**
     * Registers all static {@link Dispatch @Dispatch}-annotated methods from the given class.
     *
     * <p>Each annotated method must have exactly nTypes parameter with concrete class types.
     * Instance methods in the class are rejected with {@link InvalidDispatchException}.
     *
     * @param aclass the class containing static {@code @Dispatch} methods
     * @return this table, for chaining
     * @throws InvalidDispatchException if any annotated method is invalid
     */
    DispatchTable autoregister(Class<?> aclass);

    /**
     * Registers all {@link Dispatch @Dispatch}-annotated methods from the given instance.
     *
     * <p>Instance methods are bound to the provided object. Static methods in the same
     * class are also registered (without needing the instance). Each annotated method
     * must have exactly nTypes parameter with concrete class types.
     *
     * @param instance the object whose class is scanned and whose instance methods are bound
     * @return this table, for chaining
     * @throws InvalidDispatchException if any annotated method is invalid
     */
    DispatchTable autoregister(Object instance);

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
    Object dispatch(Object v);

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
    Object dispatch(Object v1, Object v2);

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
    Object dispatch(Object... values);
}
