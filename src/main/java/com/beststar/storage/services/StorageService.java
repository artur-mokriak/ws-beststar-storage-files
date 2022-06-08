package com.beststar.storage.services;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import com.beststar.storage.entity.FileInfoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String storeFile(MultipartFile file);
    FileInfoResponse fileInfoRandomLine(String pathFile);
    boolean fileExists(String storeFilePath);
    Path getStoreFilePath(String fileName);
    CompletableFuture<FileInfoResponse> fileInfoRandomLineAsync(String storeFilePath) throws InterruptedException;
}
