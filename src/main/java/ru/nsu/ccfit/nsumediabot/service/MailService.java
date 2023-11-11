package ru.nsu.ccfit.nsumediabot.service;

public interface MailService {
    void sendActivationMessage(String email, String token);
}
