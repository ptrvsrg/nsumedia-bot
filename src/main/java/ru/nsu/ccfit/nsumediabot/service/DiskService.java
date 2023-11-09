package ru.nsu.ccfit.nsumediabot.service;

import ru.nsu.ccfit.nsumediabot.models.dto.DiskFileDTO;

import java.io.File;

public interface DiskService {
    DiskFileDTO upload(File file);

    void delete(String diskFilePath);
}
