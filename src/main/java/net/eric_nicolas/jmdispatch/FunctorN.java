package net.eric_nicolas.jmdispatch;

/**
 * Internal functor interface for N-argument dispatch.
 *
 * <p>Implementations are generated at registration time via ASM bytecode generation.
 * Each implementation wraps a specific {@link Dispatch @Dispatch} handler method,
 * invoking it directly (via {@code invokeStatic} or {@code invokeVirtual}) with
 * appropriate casts, avoiding reflection overhead at dispatch time.
 *
 * @see DispatchTableN
 */
public interface FunctorN {

    /**
     * Invokes the underlying dispatch handler with the given arguments.
     *
     * @param values the dispatch arguments (must match the handler's arity)
     * @return the handler's return value (boxed), or {@code null} for void handlers
     */
    Object f(Object... values);
}
