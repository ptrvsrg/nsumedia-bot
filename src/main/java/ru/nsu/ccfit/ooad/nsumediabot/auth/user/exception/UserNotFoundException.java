package ru.nsu.ccfit.ooad.nsumediabot.auth.user.exception;

public class UserNotFoundException
        extends RuntimeException {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
