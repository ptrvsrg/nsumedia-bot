package ru.nsu.ccfit.nsumediabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;

@SpringBootApplication
public class NsumediaBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(NsumediaBotApplication.class, args);
	}
}
