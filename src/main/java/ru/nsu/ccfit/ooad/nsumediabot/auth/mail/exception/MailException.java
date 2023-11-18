package ru.nsu.ccfit.ooad.nsumediabot.auth.mail.exception;

public class MailException
        extends RuntimeException {

    public MailException() {
        super();
    }

    public MailException(String message) {
        super(message);
    }

    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
