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
public class MaterialDto
        implements Serializable {

    private String name;

    private String subjectName;

    private Integer subjectSemester;

    private String subjectSpecializationName;

    private String link;
}