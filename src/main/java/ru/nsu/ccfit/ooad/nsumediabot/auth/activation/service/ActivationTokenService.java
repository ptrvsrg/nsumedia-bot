package ru.nsu.ccfit.ooad.nsumediabot.auth.activation.service;

import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dto.UserDto;

public interface ActivationTokenService {

    String createToken(UserDto userDto);

    Long getChatIdByToken(String token);

    void deleteToken(String token);

    void deleteExpiredTokens();
}
