package net.eric_nicolas.jmdispatch;

/**
 * Thrown when no registered handler matches the runtime types of the dispatch arguments.
 *
 * <p>This occurs when none of the registered {@link Dispatch @Dispatch} handler parameter
 * types are assignable from the actual argument types. This includes the case where one or
 * more arguments are {@code null}, since null has no runtime type to match against.
 *
 * @see DispatchTable#dispatch(Object)
 * @see DispatchTable#dispatch(Object, Object)
 * @see DispatchTable#dispatch(Object...)
 */
public class DispatchNoMatchException extends RuntimeException {

    /**
     * @param message description of the unmatched argument types
     */
    public DispatchNoMatchException(String message) {
        super(message);
    }
}
