package ru.nsu.ccfit.ooad.nsumediabot.bot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {

    private KeyboardFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ReplyKeyboard cancelKeyboard() {
        return ReplyKeyboardMarkup.builder()
                .keyboardRow(rowWithCancelButton())
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard submitKeyboard() {
        KeyboardRow row = new KeyboardRow();
        row.add(
                KeyboardButton.builder()
                        .text(MessageUtils.CONFIRM_BUTTON)
                        .build()
        );
        row.add(
                KeyboardButton.builder()
                        .text(MessageUtils.CANCEL_BUTTON)
                        .build()
        );

        return ReplyKeyboardMarkup.builder()
                .keyboardRow(row)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard chooseSpecializationKeyboard(List<String> specializations) {
        List<KeyboardRow> rows = new ArrayList<>();
        for (String specialization : specializations) {
            KeyboardRow row = new KeyboardRow();
            row.add(KeyboardButton.builder()
                    .text(specialization)
                    .build());
            rows.add(row);
        }

        rows.add(rowWithCancelButton());

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard chooseYearKeyboard(int years) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        for (int i = 0; i < years; i++) {
            row.add(KeyboardButton.builder()
                    .text(Integer.toString(i + 1))
                    .build());
        }

        rows.add(row);
        rows.add(rowWithCancelButton());

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard chooseSemestersKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(KeyboardButton.builder()
                .text("1")
                .build());
        row.add(KeyboardButton.builder()
                .text("2")
                .build());

        rows.add(row);
        rows.add(rowWithCancelButton());

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard chooseSubjectKeyboard(List<String> subjects) {
        List<KeyboardRow> rows = new ArrayList<>();
        for (String subject : subjects) {
            KeyboardRow row = new KeyboardRow();
            row.add(KeyboardButton.builder()
                    .text(subject)
                    .build());
            rows.add(row);
        }

        rows.add(rowWithCancelButton());

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard chooseMaterialKeyboard(List<String> materials) {
        List<KeyboardRow> rows = new ArrayList<>();
        for (String material : materials) {
            KeyboardRow row = new KeyboardRow();
            row.add(KeyboardButton.builder()
                    .text(material)
                    .build());
            rows.add(row);
        }

        rows.add(rowWithCancelButton());

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard acceptOrRejectKeyboard() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text(MessageUtils.ACCEPT_BUTTON)
                .callbackData(MessageUtils.ACCEPT_BUTTON)
                .build());
        row.add(InlineKeyboardButton.builder()
                .text(MessageUtils.REJECT_BUTTON)
                .callbackData(MessageUtils.REJECT_BUTTON)
                .build());

        return InlineKeyboardMarkup.builder()
                .keyboardRow(row)
                .build();
    }

    private static KeyboardRow rowWithCancelButton() {
        return new KeyboardRow(List.of(
                KeyboardButton.builder()
                        .text(MessageUtils.CANCEL_BUTTON)
                        .build()
        ));
    }
}
