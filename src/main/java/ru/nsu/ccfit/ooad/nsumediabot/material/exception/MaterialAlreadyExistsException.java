package ru.nsu.ccfit.ooad.nsumediabot.material.exception;

public class MaterialAlreadyExistsException
        extends RuntimeException {

    public MaterialAlreadyExistsException() {
        super();
    }

    public MaterialAlreadyExistsException(String message) {
        super(message);
    }

    public MaterialAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
