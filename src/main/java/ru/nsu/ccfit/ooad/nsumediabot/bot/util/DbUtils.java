package ru.nsu.ccfit.ooad.nsumediabot.bot.util;

import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.MaterialDto;

import java.util.List;

public class DbUtils {

    public static final String USER_MAP = "USER";
    public static final String MESSAGE_MAP = "MESSAGE";
    public static final String PREVIOUS_MESSAGE_KEY = "PREVIOUS_MESSAGE";
    public static final String SPECIALIZATIONS_KEY = "SPECIALIZATIONS";
    public static final String SPECIALIZATION_KEY = "SPECIALIZATION";
    public static final String YEARS_KEY = "YEARS";
    public static final String YEAR_KEY = "YEAR";
    public static final String SEMESTER_KEY = "SEMESTER";
    public static final String SUBJECTS_KEY = "SUBJECTS";
    public static final String SUBJECT_KEY = "SUBJECT";
    private static final String MATERIALS_KEY = "MATERIALS";
    private static final String MATERIAL_KEY = "MATERIAL";
    private static final String MATERIAL_NAME_KEY = "MATERIAL_NAME";
    private static final String MATERIAL_FILE_KEY = "MATERIAL_FILE";
    private static final String CHAT_ID_KEY = "CHAT_ID";

    private DbUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    //////  Maps  //////

    public static void putToUserMap(DBContext db, Long chatId, String key, Object value) {
        db.getMap(USER_MAP + chatId.toString())
                .put(key, value);
    }

    public static <T> T getFromUserMap(DBContext db, Long chatId, String key) {
        try {
            return (T) db.getMap(USER_MAP + chatId.toString())
                    .get(key);
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static void putToMessageMap(DBContext db, Integer messageId, String key, Object value) {
        db.getMap(MESSAGE_MAP + messageId.toString())
                .put(key, value);
    }

    public static <T> T getFromMessageMap(DBContext db, Integer messageId, String key) {
        try {
            return (T) db.getMap(MESSAGE_MAP + messageId.toString())
                    .get(key);
        } catch (ClassCastException e) {
            return null;
        }
    }

    //////  User map  //////
    //  New message  //

    public static void putNewMessage(DBContext db, Update update) {
        if (!update.hasMessage()) {
            return;
        }
        putToUserMap(db, AbilityUtils.getChatId(update), PREVIOUS_MESSAGE_KEY, update.getMessage().getMessageId());
    }

    public static boolean isNewMessage(DBContext db, Update update) {
        Integer previousMessageId = getFromUserMap(db, AbilityUtils.getChatId(update), PREVIOUS_MESSAGE_KEY);
        return previousMessageId == null || !previousMessageId.equals(update.getMessage().getMessageId());
    }

    //  Select subject map  //

    public static List<String> getSpecializationsFromUserMap(DBContext db, Long chatId) {
        return getFromUserMap(db, chatId, SPECIALIZATIONS_KEY);
    }

    public static void putSpecializationsToUserMap(DBContext db, Long chatId, List<String> specializationNames) {
        putToUserMap(db, chatId, SPECIALIZATIONS_KEY, specializationNames);
    }

    public static String getSpecializationFromUserMap(DBContext db, Long chatId) {
        return getFromUserMap(db, chatId, SPECIALIZATION_KEY);
    }

    public static void putSpecializationToUserMap(DBContext db, Long chatId, String specializationName) {
        putToUserMap(db, chatId, SPECIALIZATION_KEY, specializationName);
    }

    public static Integer getYearsFromUserMap(DBContext db, Long chatId) {
        return getFromUserMap(db, chatId, YEARS_KEY);
    }

    public static void putYearsToUserMap(DBContext db, Long chatId, Integer years) {
        putToUserMap(db, chatId, YEARS_KEY, years);
    }

    public static Integer getYearFromUserMap(DBContext db, Long chatId) {
        return getFromUserMap(db, chatId, YEAR_KEY);
    }

    public static void putYearToUserMap(DBContext db, Long chatId, Integer year) {
        putToUserMap(db, chatId, YEAR_KEY, year);
    }

    public static Integer getSemesterFromUserMap(DBContext db, Long chatId) {
        return getFromUserMap(db, chatId, SEMESTER_KEY);
    }

    public static void putSemesterToUserMap(DBContext db, Long chatId, Integer semester) {
        putToUserMap(db, chatId, SEMESTER_KEY, semester);
    }

    public static List<String> getSubjectsFromUserMap(DBContext db, Long chatId) {
        return getFromUserMap(db, chatId, SUBJECTS_KEY);
    }

    public static void putSubjectsToUserMap(DBContext db, Long chatId, List<String> subjectNames) {
        putToUserMap(db, chatId, SUBJECTS_KEY, subjectNames);
    }

    public static String getSubjectFromUserMap(DBContext db, Long chatId) {
        return getFromUserMap(db, chatId, SUBJECT_KEY);
    }

    public static void putSubjectToUserMap(DBContext db, Long chatId, String subjectName) {
        putToUserMap(db, chatId, SUBJECT_KEY, subjectName);
    }

    public static List<String> getMaterialsFromUserMap(DBContext db, Long chatId) {
        return getFromUserMap(db, chatId, MATERIALS_KEY);
    }

    public static void putMaterialsToUserMap(DBContext db, Long chatId, List<String> materialNames) {
        putToUserMap(db, chatId, MATERIALS_KEY, materialNames);
    }

    public static boolean isValidSpecialization(DBContext db, Long chatId, String specializationName) {
        List<String> specializations = getSpecializationsFromUserMap(db, chatId);
        return specializations != null && specializations.contains(specializationName);
    }

    public static boolean isValidYear(DBContext db, Long chatId, String message) {
        Integer years = getYearsFromUserMap(db, chatId);
        int year;
        try {
            year = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            return false;
        }
        return years != null && year > 0 && year <= years;
    }

    public static boolean isValidSemester(String message) {
        return message.equals("1") || message.equals("2");
    }

    public static boolean isValidSubject(DBContext db, Long chatId, String subjectName) {
        List<String> subjects = getSubjectsFromUserMap(db, chatId);
        return subjects != null && subjects.contains(subjectName);
    }

    public static boolean isValidMaterial(DBContext db, Long chatId, String materialName) {
        List<String> materials = getMaterialsFromUserMap(db, chatId);
        return materials != null && materials.contains(materialName);
    }

    //  Upload material  //

    public static String getMaterialNameFromUserMap(DBContext db, Long chatId) {
        return getFromUserMap(db, chatId, MATERIAL_NAME_KEY);
    }

    public static void putMaterialNameToUserMap(DBContext db, Long chatId, String materialName) {
        putToUserMap(db, chatId, MATERIAL_NAME_KEY, materialName);
    }

    public static String getMaterialFileFromUserMap(DBContext db, Long chatId) {
        return getFromUserMap(db, chatId, MATERIAL_NAME_KEY);
    }

    public static void putMaterialFileToUserMap(DBContext db, Long chatId, String materialFile) {
        putToUserMap(db, chatId, MATERIAL_NAME_KEY, materialFile);
    }

    //////  Message map  //////

    public static MaterialDto getMaterialFromMessageMap(DBContext db, Integer messageId) {
        return getFromMessageMap(db, messageId, MATERIAL_KEY);
    }

    public static void putMaterialToMessageMap(DBContext db, Integer messageId, MaterialDto materialDto) {
        putToMessageMap(db, messageId, MATERIAL_KEY, materialDto);
    }

    public static Long getChatIdFromMessageMap(DBContext db, Integer messageId) {
        return getFromMessageMap(db, messageId, CHAT_ID_KEY);
    }

    public static void putChatIdToMessageMap(DBContext db, Integer messageId, Long chatId) {
        putToMessageMap(db, messageId, CHAT_ID_KEY, chatId);
    }
}
