package ru.nsu.ccfit.ooad.nsumediabot.bot.ability;

import lombok.RequiredArgsConstructor;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.nsu.ccfit.ooad.nsumediabot.auth.activation.exception.ActivationTokenNotFoundException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.service.AuthService;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.Role;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.exception.UserNotFoundException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.service.UserService;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.DbUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.KeyboardFactory;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.MessageUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.ReplyFactory;

@RequiredArgsConstructor
public class UnactivatedUserRoleAbility
        implements AbilityExtension {

    private final AbilityBot abilityBot;
    private final UserService userService;
    private final AuthService authService;

    public Ability activateAbility() {
        return Ability.builder()
                .name("activate")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.USER)
                .action(ctx -> {
                })
                .build();
    }

    public Reply replyToActivate() {
        Reply activateResultReply = Reply.of(
                (bot, upd) -> {
                    String token = upd.getMessage().getText();
                    try {
                        authService.activate(token);
                        bot.silent().send(MessageUtils.SUCCESS_ACTIVATE, AbilityUtils.getChatId(upd));
                    } catch (ActivationTokenNotFoundException e) {
                        bot.silent().send(MessageUtils.ACTIVATION_TOKEN_NOT_FOUND, AbilityUtils.getChatId(upd));
                    }
                },
                Flag.TEXT,
                upd -> DbUtils.isNewMessage(abilityBot.db(), upd)
        );
        ReplyFlow inputTokenReply = ReplyFlow.builder(abilityBot.db())
                .onlyIf(upd -> checkRole(AbilityUtils.getChatId(upd)))
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(bot.db(), upd);
                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.INPUT_TOKEN)
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .replyMarkup(KeyboardFactory.cancelKeyboard())
                                    .build()
                    );
                })
                .next(activateResultReply)
                .next(ReplyFactory.cancelReply())
                .build();
        return ReplyFlow.builder(abilityBot.db())
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> MessageUtils.messageEquals(upd, "/activate"))
                .next(noAccessReply())
                .next(inputTokenReply)
                .build();
    }

    private boolean checkRole(Long chatId) {
        try {
            return userService.loadUserByChatId(chatId)
                    .getRole()
                    .equals(Role.NOT_ACTIVE_USER);
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    private Reply noAccessReply() {
        return Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.NO_ACCESS_UNACTIVATED, AbilityUtils.getChatId(upd)),
                upd -> !checkRole(AbilityUtils.getChatId(upd))
        );
    }
}
