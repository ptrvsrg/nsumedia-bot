package ru.nsu.ccfit.ooad.nsumediabot.material.exception;

public class MaterialNotFoundException
        extends RuntimeException {

    public MaterialNotFoundException() {
        super();
    }

    public MaterialNotFoundException(String message) {
        super(message);
    }

    public MaterialNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
