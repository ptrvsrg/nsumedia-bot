package ru.nsu.ccfit.ooad.nsumediabot.auth.service;

import ru.nsu.ccfit.ooad.nsumediabot.auth.dto.AuthDto;

public interface AuthService {

    void register(AuthDto authDto);

    void activate(String token);
}
