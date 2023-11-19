package ru.nsu.ccfit.ooad.nsumediabot.auth.mail.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.nsu.ccfit.ooad.nsumediabot.auth.mail.exception.MailException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.mail.service.MailService;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl
        implements MailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendActivationMessage(String to, String token) {
        Context ctx = new Context();
        ctx.setVariables(Map.of(
                "username", to,
                "token", token
        ));

        String htmlText = templateEngine.process("activation-account", ctx);
        sendMessage(to, "Активация аккаунта в боте NSUMedia", htmlText);
    }

    private void sendMessage(String to, String subject, String text) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(mimeMessage);
            log.info("Letter \"{}\" was sent to {}", subject, to);
        } catch (MessagingException e) {
            log.error("Mail error", e);
            throw new MailException();
        }
    }
}

