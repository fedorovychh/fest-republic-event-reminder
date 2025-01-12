package io.service.events.exception;

public class GoogleAuthenticationException extends RuntimeException {
       public GoogleAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
