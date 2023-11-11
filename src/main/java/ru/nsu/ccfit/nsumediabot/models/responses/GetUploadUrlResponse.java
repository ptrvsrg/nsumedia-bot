package ru.nsu.ccfit.nsumediabot.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUploadUrlResponse {

    @JsonProperty("operation_id")
    private String operationId;
    private String href;
    private String method;
    private boolean templated;

}
