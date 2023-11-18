package ru.nsu.ccfit.ooad.nsumediabot.material.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ccfit.ooad.nsumediabot.material.dao.SpecializationRepository;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.SpecializationDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SpecializationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationServiceImpl
        implements SpecializationService {

    private final ModelMapper modelMapper;
    private final SpecializationRepository specializationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SpecializationDto> loadAllSpecializations() {
        return specializationRepository.findAll()
                .stream()
                .map(specialization -> modelMapper.map(specialization, SpecializationDto.class))
                .toList();
    }

    @Override
    public Integer getYearsByName(String name) {
        return specializationRepository.findByName(name)
                .orElseThrow()
                .getYears();
    }
}
