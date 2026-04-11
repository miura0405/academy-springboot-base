package com.spring.springbootapplication.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile({ "local", "test" })
public class LocalStorageService implements StorageService {

    private final Path uploadBasePath;

    public LocalStorageService(@Value("${app.upload.base:/app/uploads}") String uploadBase) {
        this.uploadBasePath = Path.of(uploadBase);
    }

    @Override
    public String uploadAvatar(MultipartFile file) throws IOException {
        String extension = extractExtension(file.getOriginalFilename());
        String key = "avatars/" + UUID.randomUUID() + extension;
        Path destination = uploadBasePath.resolve(key);

        Files.createDirectories(destination.getParent());
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination);
        }

        return key;
    }

    @Override
    public String getFileUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return "/uploads/" + key;
    }

    private String extractExtension(String fileName) {
        String baseName = StringUtils.getFilename(fileName);
        if (baseName == null || !baseName.contains(".")) {
            return "";
        }
        return baseName.substring(baseName.lastIndexOf("."));
    }
}
