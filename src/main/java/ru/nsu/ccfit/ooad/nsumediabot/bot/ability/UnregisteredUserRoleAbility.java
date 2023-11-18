package ru.nsu.ccfit.ooad.nsumediabot.bot.ability;

import lombok.RequiredArgsConstructor;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.nsu.ccfit.ooad.nsumediabot.auth.dto.AuthDto;
import ru.nsu.ccfit.ooad.nsumediabot.auth.exception.EmailNotValidException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.mail.exception.MailException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.service.AuthService;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.Role;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.exception.UserAlreadyExistsException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.exception.UserNotFoundException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.service.UserService;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.DbUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.KeyboardFactory;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.MessageUtils;
import ru.nsu.ccfit.ooad.nsumediabot.bot.util.ReplyFactory;

@RequiredArgsConstructor
public class UnregisteredUserRoleAbility
        implements AbilityExtension {

    private final AbilityBot abilityBot;
    private final UserService userService;
    private final AuthService authService;

    public Ability registerAbility() {
        return Ability.builder()
                .name("register")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                })
                .build();
    }

    public Reply replyToRegister() {
        Reply registerResultReply = Reply.of(
                (bot, upd) -> {
                    String email = upd.getMessage().getText();
                    AuthDto authDto = AuthDto.builder()
                            .chatId(AbilityUtils.getChatId(upd))
                            .email(email)
                            .build();

                    try {
                        authService.register(authDto);
                        bot.silent().send(MessageUtils.SUCCESS_REGISTER, AbilityUtils.getChatId(upd));
                    } catch (EmailNotValidException e) {
                        bot.silent().send(e.getMessage(), AbilityUtils.getChatId(upd));
                    } catch (UserAlreadyExistsException e) {
                        bot.silent().send(MessageUtils.USER_ALREADY_EXISTS, AbilityUtils.getChatId(upd));
                    } catch (MailException e) {
                        bot.silent().send(MessageUtils.SENDING_LETTER_ERROR, AbilityUtils.getChatId(upd));
                    }
                },
                Flag.TEXT,
                upd -> DbUtils.isNewMessage(abilityBot.db(), upd)
        );
        ReplyFlow inputEmailReply = ReplyFlow.builder(abilityBot.db())
                .onlyIf(upd -> checkRole(AbilityUtils.getChatId(upd)))
                .action((bot, upd) -> {
                    DbUtils.putNewMessage(bot.db(), upd);

                    bot.silent().execute(
                            SendMessage.builder()
                                    .text(MessageUtils.INPUT_EMAIL)
                                    .chatId(AbilityUtils.getChatId(upd))
                                    .replyMarkup(KeyboardFactory.cancelKeyboard())
                                    .build()
                    );
                })
                .next(registerResultReply)
                .next(ReplyFactory.cancelReply())
                .build();
        return ReplyFlow.builder(abilityBot.db())
                .onlyIf(Flag.TEXT)
                .onlyIf(upd -> MessageUtils.messageEquals(upd, "/register"))
                .next(inputEmailReply)
                .next(noAccessReply())
                .build();
    }

    private boolean checkRole(Long chatId) {
        try {
            return userService.loadUserByChatId(chatId)
                    .getRole()
                    .equals(Role.NOT_ACTIVE_USER);
        } catch (UserNotFoundException e) {
            return true;
        }
    }

    private Reply noAccessReply() {
        return Reply.of(
                (bot, upd) -> bot.silent().send(MessageUtils.NO_ACCESS_UNREGISTERED, AbilityUtils.getChatId(upd)),
                upd -> !checkRole(AbilityUtils.getChatId(upd))
        );
    }
}
