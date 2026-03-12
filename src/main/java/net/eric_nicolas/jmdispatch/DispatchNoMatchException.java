package net.eric_nicolas.jmdispatch;

public class DispatchNoMatchException extends RuntimeException {
    public DispatchNoMatchException(String message) {
        super(message);
    }
}
