package ru.nsu.ccfit.ooad.nsumediabot.auth.user.service;

import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.Role;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto loadUserByChatId(Long chatId);

    void addUser(UserDto userDto);

    void changeRoleByChatId(Long chatId, Role role);

    List<UserDto> loadAllAdmins();
}
