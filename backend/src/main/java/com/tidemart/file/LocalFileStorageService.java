package com.tidemart.file;

import com.tidemart.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/** Saves images in a local folder. No cloud storage account is needed. */
@Service
public class LocalFileStorageService {
    private static final List<String> TYPES = List.of("jpg", "jpeg", "png", "webp", "gif", "mp4", "webm");
    @Value("${tidemart.upload-dir:uploads}")
    private String dir;

    public String save(MultipartFile file) {
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1) : "";
        if (file.isEmpty() || !TYPES.contains(ext) || file.getContentType() == null || !(file.getContentType().startsWith("image/") || file.getContentType().startsWith("video/"))) throw new BadRequestException("Upload a JPG, PNG, WEBP or GIF image");
        if (file.getSize() > (ext.equals("mp4") || ext.equals("webm") ? 10L : 5L) * 1024 * 1024) throw new BadRequestException("Images must be under 5 MB and videos under 10 MB");
        try {
            Path folder = Paths.get(dir).toAbsolutePath();
            Files.createDirectories(folder);
            String name = UUID.randomUUID() + "." + ext;
            file.transferTo(folder.resolve(name));
            return "/uploads/" + name;
        } catch (IOException e) {
            throw new BadRequestException("Could not save the image");
        }
    }
}
