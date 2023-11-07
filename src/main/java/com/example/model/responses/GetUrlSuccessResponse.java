package com.example.model.responses;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUrlSuccessResponse {
    @JsonAlias({"operation_id"})
    private String operationID;
    private String href;
    private String method;
    private boolean templated;

}
