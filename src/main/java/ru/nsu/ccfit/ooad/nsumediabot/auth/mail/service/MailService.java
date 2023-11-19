package ru.nsu.ccfit.ooad.nsumediabot.auth.mail.service;

public interface MailService {
    void sendActivationMessage(String to, String token);
}
