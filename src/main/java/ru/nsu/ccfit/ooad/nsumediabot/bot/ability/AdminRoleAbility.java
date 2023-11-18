package ru.nsu.ccfit.ooad.nsumediabot.bot.ability;

import lombok.RequiredArgsConstructor;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.Role;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.exception.UserNotFoundException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.service.UserService;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.DbUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.KeyboardFactory;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.MessageUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.ReplyFactory;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.MaterialDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.SubjectDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.exception.MaterialAlreadyExistsException;
import ru.nsu.ccfit.ooad.nsumediabot.material.exception.MaterialNotFoundException;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.MaterialService;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SpecializationService;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SubjectService;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
public class AdminRoleAbility
        implements AbilityExtension {

    private final AbilityBot abilityBot;
    private final UserService userService;
    private final SpecializationService specializationService;
    private final SubjectService subjectService;
    private final MaterialService materialService;

    public Ability addMaterialBot() {
        return Ability.builder()
                .name("add_material")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.USER)
                .action(ctx -> {
                })
                .build();
    }

    public Reply replyToAddMaterial() {
        Reply addMaterialResultReply = Reply.of(
                (bot, upd) -> {
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

                    Document materialDocument = upd.getMessage().getDocument();
                    String filePath = bot.silent().execute(
                            GetFile.builder()
                                    .fileId(materialDocument.getFileId())
                                    .build()
                    ).orElseThrow().getFilePath();

                    File materialFile = new File(materialName);
                    try {
                        bot.downloadFile(filePath, materialFile);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        materialService.addMaterial(materialDto, materialFile);
                        bot.silent().send(MessageUtils.SUCCESS_ADD_MATERIAL, AbilityUtils.getChatId(upd));
                    } catch (MaterialAlreadyExistsException e) {
                        bot.silent().send(MessageUtils.MATERIAL_ALREADY_EXISTS, AbilityUtils.getChatId(upd));
                    }

                    materialFile.delete();
                },
                upd -> DbUtils.isNewMessage(abilityBot.db(), upd),
                Flag.DOCUMENT
        );
        return ReplyFlow.builder(abilityBot.db())
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> MessageUtils.messageEquals(upd, "/add_material"))
                .next(ReplyFactory.selectSubjectReply(
                        abilityBot.db(),
                        specializationService,
                        subjectService,
                        upd -> checkRole(AbilityUtils.getChatId(upd)),
                        ReplyFactory.inputMaterialReply(abilityBot.db(), addMaterialResultReply)
                ))
                .next(noAccessReply())
                .build();
    }

    public Ability deleteMaterialBot() {
        return Ability.builder()
                .name("delete_material")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.USER)
                .action(ctx -> {
                })
                .build();
    }

    public Reply replyToDeleteMaterial() {
        Reply deleteMaterialResultReply = Reply.of(
                (bot, upd) -> {
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
                    try {
                        materialService.deleteMaterial(materialDto);
                        bot.silent().send(MessageUtils.SUCCESS_DELETE_MATERIAL, AbilityUtils.getChatId(upd));
                    } catch (MaterialNotFoundException e) {
                        bot.silent().send(MessageUtils.MATERIALS_NOT_FOUND, AbilityUtils.getChatId(upd));
                    }
                },
                upd -> DbUtils.isNewMessage(abilityBot.db(), upd),
                Flag.TEXT,
                upd -> MessageUtils.messageEquals(upd, MessageUtils.CONFIRM_BUTTON)
        );
        ReplyFlow confirmReply = ReplyFlow.builder(abilityBot.db())
                .onlyIf(upd -> DbUtils.isNewMessage(abilityBot.db(), upd))
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> DbUtils.isValidMaterial(abilityBot.db(), AbilityUtils.getChatId(upd),
                        upd.getMessage().getText()))
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(bot.db(), upd);

                    String materialName = upd.getMessage().getText();
                    DbUtils.putMaterialNameToUserMap(bot.db(), AbilityUtils.getChatId(upd), materialName);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.CONFIRM_DELETION)
                                    .replyMarkup(KeyboardFactory.submitKeyboard())
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .build()
                    );
                })
                .next(deleteMaterialResultReply)
                .next(ReplyFactory.cancelReply())
                .build();
        Reply selectMaterialErrorReply = Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.MATERIAL_NOT_FOUND, AbilityUtils.getChatId(upd)),
                upd -> DbUtils.isNewMessage(abilityBot.db(), upd),
                Flag.TEXT,
                upd -> !DbUtils.isValidMaterial(abilityBot.db(), AbilityUtils.getChatId(upd),
                        upd.getMessage().getText())
        );
        ReplyFlow showMaterials = ReplyFlow.builder(abilityBot.db())
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> DbUtils.isValidSubject(abilityBot.db(), AbilityUtils.getChatId(upd),
                        upd.getMessage().getText()))
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(bot.db(), upd);

                    String specializationName =
                            DbUtils.getSpecializationFromUserMap(bot.db(), AbilityUtils.getChatId(upd));
                    Integer year = DbUtils.getYearFromUserMap(bot.db(), AbilityUtils.getChatId(upd));
                    Integer semester = DbUtils.getSemesterFromUserMap(bot.db(), AbilityUtils.getChatId(upd));
                    String subjectName = upd.getMessage().getText();
                    DbUtils.putSubjectToUserMap(bot.db(), AbilityUtils.getChatId(upd), subjectName);

                    SubjectDto subjectDto = SubjectDto.builder()
                            .specializationName(specializationName)
                            .semester((year - 1) * 2 + semester)
                            .name(subjectName)
                            .build();

                    List<String> materials = materialService.loadAllMaterials(subjectDto)
                            .stream()
                            .map(MaterialDto::getName)
                            .toList();
                    DbUtils.putMaterialsToUserMap(bot.db(), AbilityUtils.getChatId(upd), materials);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.SELECT_MATERIAL)
                                    .replyMarkup(KeyboardFactory.chooseMaterialKeyboard(materials))
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .build()
                    );
                })
                .next(confirmReply)
                .next(selectMaterialErrorReply)
                .next(ReplyFactory.cancelReply())
                .build();
        return ReplyFlow.builder(abilityBot.db())
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> MessageUtils.messageEquals(upd, "/delete_material"))
                .next(ReplyFactory.selectSubjectReply(
                        abilityBot.db(),
                        specializationService,
                        subjectService,
                        upd -> checkRole(AbilityUtils.getChatId(upd)),
                        showMaterials))
                .next(noAccessReply())
                .build();
    }

    public Reply replyToAccept() {
        return Reply.of(
                (bot, upd) -> {
                    MaterialDto materialDto = DbUtils.getMaterialFromMessageMap(
                            bot.db(),
                            upd.getCallbackQuery().getMessage().getMessageId()
                    );

                    Document materialDocument = upd.getCallbackQuery().getMessage().getDocument();
                    String filePath = bot.silent().execute(
                            GetFile.builder()
                                    .fileId(materialDocument.getFileId())
                                    .build()
                    ).orElseThrow().getFilePath();

                    File materialFile;
                    try {
                        materialFile = bot.sender().downloadFile(filePath);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        materialService.addMaterial(materialDto, materialFile);
                        bot.silent().send(MessageUtils.SUCCESS_ADD_MATERIAL, AbilityUtils.getChatId(upd));
                    } catch (MaterialAlreadyExistsException e) {
                        bot.silent().send(MessageUtils.MATERIAL_ALREADY_EXISTS, AbilityUtils.getChatId(upd));
                        materialFile.delete();
                        return;
                    }

                    materialFile.delete();

                    bot.silent().send(MessageUtils.SUCCESS_ADD_MATERIAL, AbilityUtils.getChatId(upd));
                    bot.silent().send(
                            MessageUtils.SUCCESS_ACCEPT_MATERIAL + "\n" + upd.getCallbackQuery().getMessage()
                                    .getCaption(),
                            DbUtils.getChatIdFromMessageMap(bot.db(),
                                    upd.getCallbackQuery().getMessage().getMessageId())
                    );
                    bot.silent().execute(
                            DeleteMessage.builder()
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .messageId(upd.getCallbackQuery().getMessage().getMessageId())
                                    .build()
                    );
                },
                upd -> checkRole(AbilityUtils.getChatId(upd)),
                Flag.CALLBACK_QUERY,
                MessageUtils::callbackQueryHasDocument,
                MessageUtils::callbackQueryHasCaption,
                upd -> MessageUtils.callbackDataEquals(upd, MessageUtils.ACCEPT_BUTTON)
        );
    }

    public Reply replyToReject() {
        return Reply.of(
                (bot, upd) -> {
                    bot.silent().send(MessageUtils.SUCCESS_REJECT_MATERIAL, AbilityUtils.getChatId(upd));
                    bot.silent().send(
                            MessageUtils.SUCCESS_REJECT_MATERIAL + "\n" + upd.getCallbackQuery().getMessage()
                                    .getCaption(),
                            DbUtils.getChatIdFromMessageMap(bot.db(),
                                    upd.getCallbackQuery().getMessage().getMessageId())
                    );
                    bot.silent().execute(
                            DeleteMessage.builder()
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .messageId(upd.getCallbackQuery().getMessage().getMessageId())
                                    .build()
                    );
                },
                upd -> checkRole(AbilityUtils.getChatId(upd)),
                Flag.CALLBACK_QUERY,
                MessageUtils::callbackQueryHasDocument,
                MessageUtils::callbackQueryHasCaption,
                upd -> MessageUtils.callbackDataEquals(upd, MessageUtils.REJECT_BUTTON)
        );
    }

    private boolean checkRole(Long chatId) {
        try {
            return userService.loadUserByChatId(chatId)
                    .getRole()
                    .equals(Role.ADMIN);
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    private Reply noAccessReply() {
        return Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.NO_ACCESS_ADMIN, AbilityUtils.getChatId(upd)),
                upd -> !checkRole(AbilityUtils.getChatId(upd))
        );
    }
}
