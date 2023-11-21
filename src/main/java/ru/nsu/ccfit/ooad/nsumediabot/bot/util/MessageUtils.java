package ru.nsu.ccfit.ooad.nsumediabot.bot.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageUtils {

    public static final String WELCOME = """
            Привет!
            Я помогу тебе найти необходимый учебный материал!
            Если есть, чем поделиться, регистрируйся и предлагай, мои администраторы всё проверят!""";

    public static final String ACCEPT_BUTTON = "Принять";
    public static final String REJECT_BUTTON = "Отклонить";
    public static final String CANCEL_BUTTON = "Отмена";
    public static final String CONFIRM_BUTTON = "Подтвердить";

    public static final String NO_ACCESS_ADMIN = "Эта команда доступна только администраторам";
    public static final String NO_ACCESS_REGISTERED = "Эта команда доступна только зарегистрированным пользователям";
    public static final String NO_ACCESS_UNACTIVATED = "Эта команда доступна только неактивированным пользователям";
    public static final String NO_ACCESS_UNREGISTERED =
            "Эта команда доступна только незарегистрированным пользователям";

    public static final String INPUT_EMAIL = "Введите адрес электронной почты НГУ:";
    public static final String INPUT_TOKEN = "Введите токен из письма:";
    public static final String INPUT_MATERIAL_NAME = "Введите название материала:";
    public static final String PIN_FILE = "Прикрепите файл материала:";
    public static final String CONFIRM_DELETION = "Подтвердите удаление:";
    public static final String SELECT_SPECIALIZATION = "Выберите специализацию:";
    public static final String SELECT_YEAR = "Выберите год обучения:";
    public static final String SELECT_SEMESTER = "Выберите семестр:";
    public static final String SELECT_SUBJECT = "Выберите предмет:";
    public static final String SELECT_MATERIAL = "Выберите предмет:";

    public static final String SUCCESS_REGISTER = "Вы зарегистрированы! Вам отправлено письмо с токеном для активации";
    public static final String SUCCESS_ACTIVATE =
            "Вы активировали аккаунт! Теперь есть возможность предложить свой материал";
    public static final String SUCCESS_CANCEL = "Выполнение команды отменено";
    public static final String SUCCESS_ADD_MATERIAL = "Материал успешно добавлен";
    public static final String SUCCESS_DELETE_MATERIAL = "Материал успешно удалён";
    public static final String SUCCESS_ACCEPT_MATERIAL = "Материал принят";
    public static final String SUCCESS_REJECT_MATERIAL = "Материал отклонён";
    public static final String SUCCESS_OFFER_MATERIAL = "Материал отправлен администратору на проверку";

    public static final String SPECIALIZATION_NOT_FOUND = "Такой специализации нет";
    public static final String YEAR_NOT_FOUND = "Такого года обучения нет";
    public static final String SEMESTER_NOT_FOUND = "Такого семестра нет";
    public static final String SUBJECT_NOT_FOUND = "Такого предмета нет";
    public static final String MATERIAL_NOT_FOUND = "Такого материала нет";
    public static final String MATERIALS_NOT_FOUND = "Материала нет";
    public static final String ACTIVATION_TOKEN_NOT_FOUND = "Такого токена нет, или ваш токен просрочен";
    public static final String ADMINS_NOT_FOUND = "Администраторы не обнаружены";
    public static final String USER_ALREADY_EXISTS = "Пользователь с таким email уже существует";
    public static final String MATERIAL_ALREADY_EXISTS = "Материал с таким названием уже существует";
    public static final String SENDING_LETTER_ERROR = "Не получилось отправить письмо на вашу почту";
    public static final String FILE_TOO_BIG = "Файл должен быть не более 20 МБ";
    public static final String EMAIL_NOT_VALID = "Необходим адрес электронной почты НГУ";
    public static final String DISK_ERROR = "Произошла ошибка при работе с диском";

    public static final String SPECIALIZATION_TITLE = "Специальность";
    public static final String YEAR_TITLE = "Год обучения";
    public static final String SEMESTER = "Семестр";
    public static final String SUBJECT_TITLE = "Предмет";
    public static final String MATERIAL_TITLE = "Материал";

    private MessageUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean messageEquals(Update upd, String text) {
        return upd.hasMessage() &&
                upd.getMessage().hasText() &&
                upd.getMessage().getText().equals(text);
    }

    public static boolean callbackDataEquals(Update upd, String text) {
        return upd.hasCallbackQuery() &&
                upd.getCallbackQuery().getData().equals(text);
    }

    public static boolean callbackQueryHasMessage(Update upd) {
        return upd.hasCallbackQuery() &&
                upd.getCallbackQuery().getMessage() != null;
    }

    public static boolean callbackQueryHasCaption(Update upd) {
        return callbackQueryHasMessage(upd) && upd.getCallbackQuery().getMessage().getCaption() != null;
    }

    public static boolean callbackQueryHasDocument(Update upd) {
        return callbackQueryHasMessage(upd) && upd.getCallbackQuery().getMessage().hasDocument();
    }
}
