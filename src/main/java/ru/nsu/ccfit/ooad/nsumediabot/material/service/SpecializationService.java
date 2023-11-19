package ru.nsu.ccfit.ooad.nsumediabot.material.service;

import ru.nsu.ccfit.ooad.nsumediabot.material.dto.SpecializationDto;

import java.util.List;

public interface SpecializationService {

    List<SpecializationDto> loadAllSpecializations();

    Integer getYearsByName(String name);
}
