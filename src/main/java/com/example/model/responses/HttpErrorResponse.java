package com.example.model.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HttpErrorResponse {
    private String message;
    private String description;
    private String error;
    private String reason;
    private Integer limit;

}
