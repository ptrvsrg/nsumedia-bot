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

@Service
@RequiredArgsConstructor
public class AuthServiceImpl
        implements AuthService {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ActivationTokenService activationTokenService;
    private final MailService mailService;

    @Override
    public void register(AuthDto authDto) {
        // Validation
        if (!authDto.getEmail().matches("^[\\w-\\.]+@g.nsu.ru$")) {
            throw new EmailNotValidException();
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
