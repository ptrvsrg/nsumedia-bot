package ru.nsu.ccfit.ooad.nsumediabot.material.service;

import ru.nsu.ccfit.ooad.nsumediabot.material.dto.MaterialDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.SubjectDto;

import java.io.File;
import java.util.List;

public interface MaterialService {

    List<MaterialDto> loadAllMaterials(SubjectDto subjectDto);

    void addMaterial(MaterialDto materialDto, File file);

    void deleteMaterial(MaterialDto materialDto);
}
