package ru.nsu.ccfit.ooad.nsumediabot.material.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ccfit.ooad.nsumediabot.material.dao.Material;
import ru.nsu.ccfit.ooad.nsumediabot.material.dao.MaterialRepository;
import ru.nsu.ccfit.ooad.nsumediabot.material.dao.Subject;
import ru.nsu.ccfit.ooad.nsumediabot.material.dao.SubjectRepository;
import ru.nsu.ccfit.ooad.nsumediabot.material.disk.service.DiskService;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.MaterialDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.dto.SubjectDto;
import ru.nsu.ccfit.ooad.nsumediabot.material.exception.MaterialAlreadyExistsException;
import ru.nsu.ccfit.ooad.nsumediabot.material.exception.MaterialNotFoundException;
import ru.nsu.ccfit.ooad.nsumediabot.material.service.MaterialService;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialServiceImpl
        implements MaterialService {

    private final ModelMapper modelMapper;
    private final SubjectRepository subjectRepository;
    private final MaterialRepository materialRepository;
    private final DiskService diskService;

    @Override
    @Transactional(readOnly = true)
    public List<MaterialDto> loadAllMaterials(SubjectDto subjectDto) {
        return materialRepository.findAll(
                        subjectDto.getSpecializationName(), subjectDto.getSemester(), subjectDto.getName())
                .stream()
                .map(material -> modelMapper.map(material, MaterialDto.class))
                .toList();
    }

    @Override
    @Transactional
    public void addMaterial(MaterialDto materialDto, File file) {
        if (materialRepository.exists(materialDto.getSubjectSpecializationName(), materialDto.getSubjectSemester(),
                materialDto.getSubjectName(), materialDto.getName())) {
            log.error("Material {}/{}/{}/{}/{} already exists", materialDto.getSubjectSpecializationName(),
                    (materialDto.getSubjectSemester() + 1) / 2, (materialDto.getSubjectSemester() + 1) % 2 + 1,
                    materialDto.getSubjectName(), materialDto.getName());
            throw new MaterialAlreadyExistsException();
        }

        Subject subject = subjectRepository.findOne(materialDto.getSubjectSpecializationName(),
                materialDto.getSubjectSemester(),
                materialDto.getSubjectName()).orElseThrow();

        String link = diskService.uploadFile(getDiskPath(materialDto), file);

        Material material = modelMapper.map(materialDto, Material.class);
        material.setLink(link);
        material.setSubject(subject);

        materialRepository.save(material);
        log.info("Material {} added", getDiskPath(materialDto));
    }

    @Override
    @Transactional
    public void deleteMaterial(MaterialDto materialDto) {
        if (!materialRepository.exists(materialDto.getSubjectSpecializationName(), materialDto.getSubjectSemester(),
                materialDto.getSubjectName(), materialDto.getName())) {
            log.error("Material {}/{}/{}/{}/{} not found", materialDto.getSubjectSpecializationName(),
                    (materialDto.getSubjectSemester() + 1) / 2, (materialDto.getSubjectSemester() + 1) % 2 + 1,
                    materialDto.getSubjectName(), materialDto.getName());
            throw new MaterialNotFoundException();
        }

        diskService.deleteFile(getDiskPath(materialDto));

        materialRepository.delete(materialDto.getSubjectSpecializationName(), materialDto.getSubjectSemester(),
                materialDto.getSubjectName(), materialDto.getName());
        log.info("Material {} deleted", getDiskPath(materialDto));
    }

    private String getDiskPath(MaterialDto materialDto) {
        StringBuilder path = new StringBuilder();
        path.append(materialDto.getSubjectSpecializationName());
        path.append("/");
        path.append((materialDto.getSubjectSemester() + 1) / 2);
        path.append("/");
        path.append(materialDto.getSubjectSemester() % 2);
        path.append("/");
        path.append(materialDto.getSubjectName());
        path.append("/");
        path.append(materialDto.getName());
        return path.toString();
    }
}
