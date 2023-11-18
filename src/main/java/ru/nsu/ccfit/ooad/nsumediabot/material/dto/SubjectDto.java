package ru.nsu.ccfit.ooad.nsumediabot.material.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDto
        implements Serializable {

    private String name;

    private Integer year;

    private Integer semester;

    private String specializationName;
}