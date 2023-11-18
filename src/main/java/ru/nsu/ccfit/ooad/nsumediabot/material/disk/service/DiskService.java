package ru.nsu.ccfit.ooad.nsumediabot.material.disk.service;

import java.io.File;

public interface DiskService {

    String uploadFile(String path, File file);

    void deleteFile(String path);
}
