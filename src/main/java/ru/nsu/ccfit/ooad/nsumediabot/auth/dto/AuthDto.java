package ru.nsu.ccfit.ooad.nsumediabot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto
        implements Serializable {

    private Long chatId;

    private String email;
}