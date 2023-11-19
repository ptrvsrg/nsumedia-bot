package ru.nsu.ccfit.ooad.nsumediabot.bot.util;

import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.objects.ReplyFlow;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.SpecializationDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.SubjectDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SpecializationService;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SubjectService;

import java.util.List;
import java.util.function.Predicate;

public class ReplyFactory {

    private ReplyFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    //////  Cancel reply  //////

    public static Reply cancelReply() {
        return Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.SUCCESS_CANCEL, AbilityUtils.getChatId(upd)),
                Flag.TEXT,
                upd -> MessageUtils.messageEquals(upd, MessageUtils.CANCEL_BUTTON)
        );
    }

    //////  Select subject reply flow  //////
    public static ReplyFlow selectSubjectReply(DBContext db, SpecializationService specializationService,
            SubjectService subjectService, Predicate<Update> condition, ReplyFlow next) {
        return showSpecializationReply(db, specializationService, subjectService, condition, next);
    }

    private static Reply selectSpecializationErrorReply(DBContext db) {
        return Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.SPECIALIZATION_NOT_FOUND, AbilityUtils.getChatId(upd)),
                upd -> DbUtils.isNewMessage(db, upd),
                upd -> !MessageUtils.messageEquals(upd, MessageUtils.CANCEL_BUTTON),
                upd -> !upd.hasMessage() || !upd.getMessage().hasText() ||
                        !DbUtils.isValidSpecialization(db, AbilityUtils.getChatId(upd), upd.getMessage().getText())
        );
    }

    private static Reply selectYearsErrorReply(DBContext db) {
        return Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.YEAR_NOT_FOUND, AbilityUtils.getChatId(upd)),
                upd -> DbUtils.isNewMessage(db, upd),
                upd -> !MessageUtils.messageEquals(upd, MessageUtils.CANCEL_BUTTON),
                upd -> !upd.hasMessage() || !upd.getMessage().hasText() ||
                        !DbUtils.isValidYear(db, AbilityUtils.getChatId(upd), upd.getMessage().getText())
        );
    }

    private static Reply selectSemestersErrorReply(DBContext db) {
        return Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.SEMESTER_NOT_FOUND, AbilityUtils.getChatId(upd)),
                upd -> DbUtils.isNewMessage(db, upd),
                upd -> !MessageUtils.messageEquals(upd, MessageUtils.CANCEL_BUTTON),
                upd -> !upd.hasMessage() || !upd.getMessage().hasText() ||
                        !DbUtils.isValidSemester(upd.getMessage().getText())
        );
    }

    private static Reply selectSubjectsErrorReply(DBContext db) {
        return Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.SUBJECT_NOT_FOUND, AbilityUtils.getChatId(upd)),
                upd -> DbUtils.isNewMessage(db, upd),
                upd -> !MessageUtils.messageEquals(upd, MessageUtils.CANCEL_BUTTON),
                upd -> !upd.hasMessage() || !upd.getMessage().hasText() ||
                        !DbUtils.isValidSubject(db, AbilityUtils.getChatId(upd), upd.getMessage().getText())
        );
    }

    private static ReplyFlow showSpecializationReply(DBContext db, SpecializationService specializationService,
            SubjectService subjectService, Predicate<Update> condition,
            ReplyFlow next) {
        return ReplyFlow.builder(db)
                .onlyIf(Flag.TEXT)
                .onlyIf(condition)
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(db, upd);

                    List<String> specializationNames = specializationService.loadAllSpecializations()
                            .stream()
                            .map(SpecializationDto::getName)
                            .toList();
                    DbUtils.putSpecializationsToUserMap(db, AbilityUtils.getChatId(upd), specializationNames);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.SELECT_SPECIALIZATION)
                                    .replyMarkup(KeyboardFactory.chooseSpecializationKeyboard(specializationNames))
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .build()
                    );
                })
                .next(showYearsReply(db, specializationService, subjectService, next))
                .next(selectSpecializationErrorReply(db))
                .next(cancelReply())
                .build();
    }

    private static ReplyFlow showYearsReply(DBContext db, SpecializationService specializationService,
            SubjectService subjectService, ReplyFlow next) {
        return ReplyFlow.builder(db)
                .onlyIf(upd -> DbUtils.isNewMessage(db, upd))
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> DbUtils.isValidSpecialization(db, AbilityUtils.getChatId(upd),
                        upd.getMessage().getText()))
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(db, upd);

                    String specializationName = upd.getMessage().getText();
                    DbUtils.putSpecializationToUserMap(db, AbilityUtils.getChatId(upd), specializationName);

                    Integer years = specializationService.getYearsByName(specializationName);
                    DbUtils.putYearsToUserMap(db, AbilityUtils.getChatId(upd), years);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.SELECT_YEAR)
                                    .replyMarkup(KeyboardFactory.chooseYearKeyboard(years))
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .build()
                    );
                })
                .next(showSemestersReply(db, subjectService, next))
                .next(selectYearsErrorReply(db))
                .next(cancelReply())
                .build();
    }

    private static ReplyFlow showSemestersReply(DBContext db, SubjectService subjectService, ReplyFlow next) {
        return ReplyFlow.builder(db)
                .onlyIf(upd -> DbUtils.isNewMessage(db, upd))
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> DbUtils.isValidYear(db, AbilityUtils.getChatId(upd), upd.getMessage().getText()))
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(db, upd);

                    int year = Integer.parseInt(upd.getMessage().getText());
                    DbUtils.putYearToUserMap(db, AbilityUtils.getChatId(upd), year);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.SELECT_SEMESTER)
                                    .replyMarkup(KeyboardFactory.chooseSemestersKeyboard())
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .build()
                    );
                })
                .next(showSubjectsReply(db, subjectService, next))
                .next(selectSemestersErrorReply(db))
                .next(cancelReply())
                .build();
    }

    private static ReplyFlow showSubjectsReply(DBContext db, SubjectService subjectService, ReplyFlow next) {
        return ReplyFlow.builder(db)
                .onlyIf(upd -> DbUtils.isNewMessage(db, upd))
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> DbUtils.isValidSemester(upd.getMessage().getText()))
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(db, upd);

                    int semester = Integer.parseInt(upd.getMessage().getText());
                    DbUtils.putSemesterToUserMap(db, AbilityUtils.getChatId(upd), semester);

                    Integer year = DbUtils.getYearFromUserMap(db, AbilityUtils.getChatId(upd));
                    String specializationName = DbUtils.getSpecializationFromUserMap(db, AbilityUtils.getChatId(upd));

                    List<String> subjectNames =
                            subjectService.loadAllSubjects(specializationName, (year - 1) * 2 + semester)
                                    .stream()
                                    .map(SubjectDto::getName)
                                    .toList();
                    DbUtils.putSubjectsToUserMap(db, AbilityUtils.getChatId(upd), subjectNames);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.SELECT_SUBJECT)
                                    .replyMarkup(KeyboardFactory.chooseSubjectKeyboard(subjectNames))
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .build()
                    );
                })
                .next(next)
                .next(selectSubjectsErrorReply(db))
                .next(cancelReply())
                .build();
    }

    ////// Input material //////

    public static ReplyFlow inputMaterialReply(DBContext db, Reply next) {
        ReplyFlow pinFileReply = ReplyFlow.builder(db)
                .onlyIf(upd -> DbUtils.isNewMessage(db, upd))
                .onlyIf(Flag.TEXT)
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(bot.db(), upd);

                    String materialName = upd.getMessage().getText();
                    DbUtils.putMaterialNameToUserMap(bot.db(), AbilityUtils.getChatId(upd), materialName);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.PIN_FILE)
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .replyMarkup(KeyboardFactory.cancelKeyboard())
                                    .build()
                    );
                })
                .next(next)
                .next(ReplyFactory.cancelReply())
                .build();
        return ReplyFlow.builder(db)
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> DbUtils.isValidSubject(db, AbilityUtils.getChatId(upd),
                        upd.getMessage().getText()))
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(bot.db(), upd);

                    String subjectName = upd.getMessage().getText();
                    DbUtils.putSubjectToUserMap(bot.db(), AbilityUtils.getChatId(upd), subjectName);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.INPUT_MATERIAL_NAME)
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .replyMarkup(KeyboardFactory.cancelKeyboard())
                                    .build()
                    );
                })
                .next(pinFileReply)
                .next(ReplyFactory.cancelReply())
                .build();
    }
}
