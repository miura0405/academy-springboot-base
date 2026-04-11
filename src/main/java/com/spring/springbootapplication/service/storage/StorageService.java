package com.spring.springbootapplication.service.storage;

import java.io.IOException;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadAvatar(MultipartFile file) throws IOException;

    String getFileUrl(String key);

    default String resolveAvatarUrl(String avatar) {
        if (avatar == null || avatar.isBlank()) {
            return null;
        }
        if (avatar.startsWith("avatars/")) {
            return getFileUrl(avatar);
        }
        return "/uploads/avatars/" + avatar;
    }

    default String extractExtension(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        return extension != null ? "." + extension : "";
    }
}
