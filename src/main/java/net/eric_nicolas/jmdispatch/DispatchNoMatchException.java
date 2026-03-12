package net.eric_nicolas.jmdispatch;

public class DispatchNoMatchException extends RuntimeException {
    DispatchNoMatchException(String message) {
        super(message);
    }
}
