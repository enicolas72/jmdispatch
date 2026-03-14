package net.eric_nicolas.jmdispatch;

/**
 * Thrown when handler registration is invalid.
 *
 * <p>This exception is raised at registration time (during {@code autoregister}) when
 * a {@link Dispatch @Dispatch} method violates one of the following constraints:
 * <ul>
 *   <li>The method is abstract (only concrete methods are allowed)</li>
 *   <li>A parameter type is an interface or abstract class (only concrete classes are allowed)</li>
 *   <li>An instance method is registered via {@code autoregister(Class)} instead of
 *       {@code autoregister(instance)}</li>
 *   <li>A handler with the same parameter type signature is already registered</li>
 * </ul>
 *
 * @see DispatchTable2#autoregister(Class)
 * @see DispatchTableN#autoregister(Class)
 */
public class InvalidDispatchException extends RuntimeException {

    /**
     * @param message description of the registration error
     */
    public InvalidDispatchException(String message) {
        super(message);
    }

    /**
     * @param message description of the registration error
     * @param cause the underlying cause
     */
    public InvalidDispatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
