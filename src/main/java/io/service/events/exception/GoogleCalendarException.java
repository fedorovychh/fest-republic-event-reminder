package io.service.events.exception;

public class GoogleCalendarException extends RuntimeException {
    public GoogleCalendarException(String message, Throwable cause) {
        super(message, cause);
    }
}
