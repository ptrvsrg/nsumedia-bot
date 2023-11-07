package com.example.service;

import java.io.File;

public interface DiskService {
    String upload(File file);

    String delete(String link);
}
