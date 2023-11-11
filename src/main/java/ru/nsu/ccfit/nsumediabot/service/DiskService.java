package ru.nsu.ccfit.nsumediabot.service;

import java.io.File;

public interface DiskService {

    String uploadFile(String path, File file);

    void deleteFile(String path);
}
