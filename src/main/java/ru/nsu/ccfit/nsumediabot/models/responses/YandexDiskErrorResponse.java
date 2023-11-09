package ru.nsu.ccfit.nsumediabot.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YandexDiskErrorResponse {
    private String message;
    private String description;
    private String error;
    private String reason;
    private Integer limit;

}
