package ru.nsu.ccfit.ooad.nsumediabot.bot.ability;

import lombok.RequiredArgsConstructor;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.Role;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dto.UserDto;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.exception.UserNotFoundException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.service.UserService;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.DbUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.KeyboardFactory;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.MessageUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.ReplyFactory;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.MaterialDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SpecializationService;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SubjectService;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
public class RegisteredUserRoleAbility
        implements AbilityExtension {

    private final AbilityBot abilityBot;
    private final UserService userService;
    private final SpecializationService specializationService;
    private final SubjectService subjectService;

    private int adminNumber = 0;

    public Ability offerMaterialBot() {
        return Ability.builder()
                .name("offer_material")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.USER)
                .action(ctx -> {
                })
                .build();
    }

    public Reply replyToOfferMaterial() {
        Reply offerMaterialResultReply = Reply.of(
                (bot, upd) -> {
                    // Create material
                    String specializationName =
                            DbUtils.getSpecializationFromUserMap(bot.db(), AbilityUtils.getChatId(upd));
                    Integer year = DbUtils.getYearFromUserMap(bot.db(), AbilityUtils.getChatId(upd));
                    Integer semester = DbUtils.getSemesterFromUserMap(bot.db(), AbilityUtils.getChatId(upd));
                    String subjectName = DbUtils.getSubjectFromUserMap(bot.db(), AbilityUtils.getChatId(upd));
                    String materialName = DbUtils.getMaterialNameFromUserMap(bot.db(), AbilityUtils.getChatId(upd));
                    MaterialDto materialDto = MaterialDto.builder()
                            .subjectSpecializationName(specializationName)
                            .subjectSemester((year - 1) * 2 + semester)
                            .subjectName(subjectName)
                            .name(materialName)
                            .build();

                    // Choose admin
                    List<Long> adminChatIds = userService.loadAllAdmins()
                            .stream()
                            .map(UserDto::getChatId)
                            .toList();
                    if (adminChatIds.isEmpty()) {
                        bot.silent().send(MessageUtils.ADMINS_NOT_FOUND, AbilityUtils.getChatId(upd));
                        return;
                    }
                    Long adminChatId = adminChatIds.get(adminNumber % adminChatIds.size());
                    adminNumber = (adminNumber + 1) % adminChatIds.size();

                    // Download material file
                    Document materialDocument = upd.getMessage().getDocument();
                    String filePath = bot.silent().execute(
                            GetFile.builder()
                                    .fileId(materialDocument.getFileId())
                                    .build()
                    ).orElseThrow().getFilePath();

                    File materialFile = new File(materialDocument.getFileName());
                    try {
                        bot.downloadFile(filePath, materialFile);
                    } catch (TelegramApiException e) {
                        materialFile.delete();
                        throw new RuntimeException(e);
                    }

                    // Send to admin
                    Message message;
                    try {
                        message = bot.sender().sendDocument(
                                SendDocument.builder()
                                        .document(new InputFile(materialFile))
                                        .caption(
                                                MessageUtils.SPECIALIZATION_TITLE + ": " + specializationName + "\n" +
                                                        MessageUtils.YEAR_TITLE + ": " + year + "\n" +
                                                        MessageUtils.SEMESTER + ": " + semester + "\n" +
                                                        MessageUtils.SUBJECT_TITLE + ": " + subjectName + "\n" +
                                                        MessageUtils.MATERIAL_TITLE + ": " + materialName
                                        )
                                        .replyMarkup(KeyboardFactory.acceptOrRejectKeyboard())
                                        .chatId(adminChatId)
                                        .build()
                        );
                    } catch (TelegramApiException e) {
                        materialFile.delete();
                        throw new RuntimeException(e);
                    }
                    materialFile.delete();

                    DbUtils.putMaterialToMessageMap(bot.db(), message.getMessageId(), materialDto);
                    DbUtils.putChatIdToMessageMap(bot.db(), message.getMessageId(), AbilityUtils.getChatId(upd));

                    bot.silent().send(MessageUtils.SUCCESS_OFFER_MATERIAL, AbilityUtils.getChatId(upd));
                },
                upd -> DbUtils.isNewMessage(abilityBot.db(), upd),
                Flag.DOCUMENT
        );
        ReplyFlow pinFileReply = ReplyFlow.builder(abilityBot.db())
                .onlyIf(upd -> DbUtils.isNewMessage(abilityBot.db(), upd))
                .onlyIf(Flag.TEXT)
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(bot.db(), upd);

                    String materialName = upd.getMessage().getText();
                    DbUtils.putMaterialNameToUserMap(bot.db(), AbilityUtils.getChatId(upd), materialName);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.PIN_FILE)
                                    .replyMarkup(KeyboardFactory.submitKeyboard())
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .build()
                    );
                })
                .next(offerMaterialResultReply)
                .next(ReplyFactory.cancelReply())
                .build();
        ReplyFlow inputMaterialNameReply = ReplyFlow.builder(abilityBot.db())
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> DbUtils.isValidSubject(abilityBot.db(), AbilityUtils.getChatId(upd),
                        upd.getMessage().getText()))
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(bot.db(), upd);

                    String subjectName = upd.getMessage().getText();
                    DbUtils.putSubjectToUserMap(bot.db(), AbilityUtils.getChatId(upd), subjectName);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.INPUT_MATERIAL_NAME)
                                    .replyMarkup(KeyboardFactory.submitKeyboard())
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .build()
                    );
                })
                .next(pinFileReply)
                .next(ReplyFactory.cancelReply())
                .build();
        Reply noAdminsReply = Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.ADMINS_NOT_FOUND, AbilityUtils.getChatId(upd)),
                upd -> userService.loadAllAdmins().isEmpty()
        );
        return ReplyFlow.builder(abilityBot.db())
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> MessageUtils.messageEquals(upd, "/offer_material"))
                .next(ReplyFactory.selectSubjectReply(
                        abilityBot.db(),
                        specializationService,
                        subjectService,
                        upd -> checkRole(AbilityUtils.getChatId(upd)) && !userService.loadAllAdmins().isEmpty(),
                        inputMaterialNameReply
                ))
                .next(noAdminsReply)
                .next(noAccessReply())
                .build();
    }

    private boolean checkRole(Long chatId) {
        Role role;
        try {
            role = userService.loadUserByChatId(chatId).getRole();
        } catch (UserNotFoundException e) {
            return false;
        }

        return role.equals(Role.ACTIVE_USER) || role.equals(Role.ADMIN);
    }

    private Reply noAccessReply() {
        return Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.NO_ACCESS_REGISTERED, AbilityUtils.getChatId(upd)),
                upd -> !checkRole(AbilityUtils.getChatId(upd))
        );
    }
}
