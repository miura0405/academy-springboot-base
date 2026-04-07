package com.spring.springbootapplication.service.storage;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadAvatar(MultipartFile file) throws IOException;

    String getFileUrl(String key);
}
