package ru.nsu.ccfit.ooad.nsumediabot.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.ooad.nsumediabot.auth.activation.service.ActivationTokenService;
import ru.nsu.ccfit.ooad.nsumediabot.auth.dto.AuthDto;
import ru.nsu.ccfit.ooad.nsumediabot.auth.exception.EmailNotValidException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.mail.service.MailService;
import ru.nsu.ccfit.ooad.nsumediabot.auth.service.AuthService;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.Role;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dto.UserDto;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl
        implements AuthService {

    private static final String EMAIL_TOO_LONG_ERROR = "Адрес электронной почты должен быть не длиннее 255 символов";
    private static final String NSU_EMAIL_NOT_VALID_ERROR = "Необходим адрес электронной почты НГУ";

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ActivationTokenService activationTokenService;
    private final MailService mailService;

    @Override
    public void register(AuthDto authDto) {
        // Validation
        List<String> errors = new ArrayList<>();
        if (authDto.getEmail().length() > 255) {
            errors.add(EMAIL_TOO_LONG_ERROR);
        }
        if (!authDto.getEmail().matches("^[\\w-\\.]+@g.nsu.ru$")) {
            errors.add(NSU_EMAIL_NOT_VALID_ERROR);
        }
        if (!errors.isEmpty()) {
            throw new EmailNotValidException(
                    errors.stream()
                            .reduce((v1, v2) -> v1 + ";\n" + v2)
                            .get()
            );
        }

        // Delete expired tokens and users
        activationTokenService.deleteExpiredTokens();

        // Add user
        UserDto userDto = modelMapper.map(authDto, UserDto.class);
        userDto.setRole(Role.NOT_ACTIVE_USER);
        userService.addUser(userDto);

        // Send created token to mail
        String token = activationTokenService.createToken(userDto);
        mailService.sendActivationMessage(authDto.getEmail(), token);
    }

    @Override
    public void activate(String token) {
        Long chatId = activationTokenService.getChatIdByToken(token);
        userService.changeRoleByChatId(chatId, Role.ACTIVE_USER);
        activationTokenService.deleteToken(token);
    }
}
