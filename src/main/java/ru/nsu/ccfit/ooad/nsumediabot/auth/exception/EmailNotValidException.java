package ru.nsu.ccfit.ooad.nsumediabot.auth.exception;

public class EmailNotValidException
        extends RuntimeException {

    public EmailNotValidException() {
        super();
    }

    public EmailNotValidException(String message) {
        super(message);
    }

    public EmailNotValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
