package net.eric_nicolas.jmdispatch;

/**
 * Thrown when multiple registered handlers match the dispatch arguments with equal distance.
 *
 * <p>The dispatch algorithm computes inheritance distance from each actual argument type to
 * each registered handler's parameter types and ranks matches by Euclidean norm (sum of
 * squared distances). If two or more handlers produce the same sorted distance vector,
 * the call is ambiguous and this exception is thrown.
 *
 * <p>To resolve ambiguity, register a more specific handler whose parameter types exactly
 * match (or more closely match) the argument types in question.
 *
 * @see DispatchTable2#dispatch(Object, Object)
 * @see DispatchTableN#dispatch(Object...)
 */
public class DispatchAmbiguousException extends RuntimeException {

    /**
     * @param message description of the ambiguous argument types and candidate handlers
     */
    public DispatchAmbiguousException(String message) {
        super(message);
    }
}
