package ru.nsu.ccfit.nsumediabot.services;

public interface MailService {
    void sendActivationMessage(String to, String token);
}
