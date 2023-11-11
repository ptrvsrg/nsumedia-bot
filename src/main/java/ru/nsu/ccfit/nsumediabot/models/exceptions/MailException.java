package ru.nsu.ccfit.nsumediabot.models.exceptions;

public class MailException extends RuntimeException{
    public MailException(String message) {
        super(message);
    }
}
