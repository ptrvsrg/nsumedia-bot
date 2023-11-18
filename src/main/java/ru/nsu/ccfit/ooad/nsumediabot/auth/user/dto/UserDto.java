package ru.nsu.ccfit.ooad.nsumediabot.auth.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.Role;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto
        implements Serializable {

    private Long chatId;

    private String email;

    private Role role;
}