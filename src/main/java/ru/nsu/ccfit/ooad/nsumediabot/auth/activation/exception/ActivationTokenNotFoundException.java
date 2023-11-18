package ru.nsu.ccfit.ooad.nsumediabot.auth.activation.exception;

public class ActivationTokenNotFoundException
        extends RuntimeException {

    public ActivationTokenNotFoundException() {
        super();
    }

    public ActivationTokenNotFoundException(String message) {
        super(message);
    }

    public ActivationTokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
