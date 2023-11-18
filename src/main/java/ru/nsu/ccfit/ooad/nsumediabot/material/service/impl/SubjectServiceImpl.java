package ru.nsu.ccfit.ooad.nsumediabot.material.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.ooad.nsumediabot.material.dao.SubjectRepository;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.SubjectDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.SubjectService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl
        implements SubjectService {

    private final ModelMapper modelMapper;
    private final SubjectRepository subjectRepository;

    @Override
    public List<SubjectDto> loadAllSubjects(String specializationName, Integer semester) {
        return subjectRepository.findAllBySpecializationAndSemester(specializationName, semester)
                .stream()
                .map(subject -> modelMapper.map(subject, SubjectDto.class))
                .toList();
    }
}
