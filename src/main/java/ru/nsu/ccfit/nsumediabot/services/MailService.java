package ru.nsu.ccfit.nsumediabot.services;

public interface MailService {
    void sendActivationMessage(String email, String token);
}
