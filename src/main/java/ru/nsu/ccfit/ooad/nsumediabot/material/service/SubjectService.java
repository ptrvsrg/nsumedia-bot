package ru.nsu.ccfit.ooad.nsumediabot.material.service;

import ru.nsu.ccfit.ooad.nsumediabot.material.dto.SubjectDto;

import java.util.List;

public interface SubjectService {

    List<SubjectDto> loadAllSubjects(String specializationName, Integer semester);
}
