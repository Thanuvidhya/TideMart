package com.tidemart.file;

import com.tidemart.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final LocalFileStorageService storage;

    public FileController(LocalFileStorageService storage) { this.storage = storage; }

    @PostMapping
    public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) { return ApiResponse.ok(Map.of("url", storage.save(file))); }
}
