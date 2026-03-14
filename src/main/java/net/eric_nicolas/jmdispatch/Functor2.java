package net.eric_nicolas.jmdispatch;

/**
 * Internal functor interface for 2-argument dispatch.
 *
 * <p>Implementations are generated at registration time via ASM bytecode generation.
 * Each implementation wraps a specific {@link Dispatch @Dispatch} handler method,
 * invoking it directly (via {@code invokeStatic} or {@code invokeVirtual}) with
 * appropriate casts, avoiding reflection overhead at dispatch time.
 *
 * @see DispatchTable2
 */
public interface Functor2 {

    /**
     * Invokes the underlying dispatch handler with the given arguments.
     *
     * @param a the first dispatch argument
     * @param b the second dispatch argument
     * @return the handler's return value (boxed), or {@code null} for void handlers
     */
    Object f(Object a, Object b);
}
