package ru.nsu.ccfit.nsumediabot.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublishFileResponse {
    private String href;
    private String method;
    private boolean templated;

}
