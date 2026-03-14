package net.eric_nicolas.jmdispatch;

public class InvalidDispatchException extends RuntimeException {
    public InvalidDispatchException(String message) {
        super(message);
    }

    public InvalidDispatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
