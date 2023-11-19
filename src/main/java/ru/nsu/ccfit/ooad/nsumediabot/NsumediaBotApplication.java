package ru.nsu.ccfit.ooad.nsumediabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.nsu.ccfit.ooad.nsumediabot.bot.NsumediaBot;

@SpringBootApplication
public class NsumediaBotApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(NsumediaBotApplication.class, args);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(ctx.getBean("nsumediaBot", NsumediaBot.class));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
