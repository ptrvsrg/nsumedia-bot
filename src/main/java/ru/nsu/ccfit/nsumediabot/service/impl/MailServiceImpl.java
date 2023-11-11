package ru.nsu.ccfit.nsumediabot.service.impl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.nsu.ccfit.nsumediabot.models.exceptions.MailException;
import ru.nsu.ccfit.nsumediabot.service.MailService;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void sendMessage(String to, String subject, String text) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(mimeMessage);
            log.info("Letter \"{}\" was sent to {}", subject, to);
        } catch (MessagingException exception) {
            log.error("Mail error", exception.getLocalizedMessage());
            throw new MailException(exception.getLocalizedMessage());
        }
    }

    @Override
    public void sendActivationMessage(String email, String token) {
        Context context = new Context();

        context.setVariables(Map.of("username", email,
                "token", token));

        String htmlText = templateEngine.process("get-account-token", context);
        sendMessage(email, "Welcome and take activation token", htmlText);
    }

}

