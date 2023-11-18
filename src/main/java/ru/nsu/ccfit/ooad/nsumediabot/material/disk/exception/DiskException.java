package ru.nsu.ccfit.ooad.nsumediabot.material.disk.exception;

public class DiskException
        extends RuntimeException {

    public DiskException() {
        super();
    }

    public DiskException(String message) {
        super(message);
    }

    public DiskException(String message, Throwable cause) {
        super(message, cause);
    }
}
