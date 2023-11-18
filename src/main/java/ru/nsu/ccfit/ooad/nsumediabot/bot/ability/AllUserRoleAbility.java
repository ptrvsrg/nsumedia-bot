package ru.nsu.ccfit.ooad.nsumediabot.bot.ability;

import lombok.RequiredArgsConstructor;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.DbUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.MessageUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.ReplyFactory;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.SubjectDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.MaterialService;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SpecializationService;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SubjectService;

@RequiredArgsConstructor
public class AllUserRoleAbility
        implements AbilityExtension {

    private final AbilityBot abilityBot;
    private final SpecializationService specializationService;
    private final SubjectService subjectService;
    private final MaterialService materialService;

    public Ability startAbility() {
        return Ability.builder()
                .name("start")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.USER)
                .action(ctx -> abilityBot.silent().send(MessageUtils.WELCOME, ctx.chatId()))
                .build();
    }

    public Ability showMaterialAbility() {
        return Ability.builder()
                .name("show_material")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.USER)
                .action(ctx -> {
                })
                .build();
    }

    public Reply replyToShowMaterial() {
        ReplyFlow showMaterial = ReplyFlow.builder(abilityBot.db())
                .onlyIf(upd -> DbUtils.isNewMessage(abilityBot.db(), upd))
                .onlyIf(upd -> DbUtils.isValidSubject(abilityBot.db(), AbilityUtils.getChatId(upd),
                        upd.getMessage().getText()))
                .action((bot, upd) -> {
                    String subjectName = upd.getMessage().getText();

                    Integer year = DbUtils.getYearFromUserMap(bot.db(), AbilityUtils.getChatId(upd));
                    Integer semester = DbUtils.getSemesterFromUserMap(bot.db(), AbilityUtils.getChatId(upd));
                    String specializationName =
                            DbUtils.getSpecializationFromUserMap(bot.db(), AbilityUtils.getChatId(upd));

                    SubjectDto subjectDto = new SubjectDto();
                    subjectDto.setName(subjectName);
                    subjectDto.setSemester((year - 1) * 2 + semester);
                    subjectDto.setSpecializationName(specializationName);

                    StringBuilder messageBuilder = new StringBuilder();
                    materialService.loadAllMaterials(subjectDto)
                            .forEach(materialDto -> {
                                messageBuilder.append("+ [");
                                messageBuilder.append(materialDto.getName());
                                messageBuilder.append("](");
                                messageBuilder.append(materialDto.getLink());
                                messageBuilder.append(")\n");
                            });
                    if (messageBuilder.isEmpty()) {
                        messageBuilder.append(MessageUtils.MATERIALS_NOT_FOUND);
                    }

                    bot.silent().sendMd(messageBuilder.toString(), AbilityUtils.getChatId(upd));
                })
                .build();
        return ReplyFlow.builder(abilityBot.db())
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> MessageUtils.messageEquals(upd, "/show_material"))
                .next(ReplyFactory.selectSubjectReply(
                        abilityBot.db(),
                        specializationService,
                        subjectService,
                        upd -> true,
                        showMaterial
                ))
                .build();
    }
}
