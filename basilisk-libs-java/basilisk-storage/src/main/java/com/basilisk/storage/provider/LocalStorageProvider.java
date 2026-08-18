package com.basilisk.storage.provider;

import com.basilisk.storage.properties.StorageProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

/**
 * Provider que armazena arquivos no filesystem local.
 * O diretório base é configurável via basilisk.storage.local.base-dir.
 */
@Slf4j
public class LocalStorageProvider implements StorageProvider {

    private final Path basePath;

    public LocalStorageProvider(StorageProperties properties) {
        this.basePath = Paths.get(properties.getLocal().getBaseDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(basePath);
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível criar o diretório base de storage: " + basePath, ex);
        }
    }

    @Override
    public String store(MultipartFile file, String directory) {
        try {
            Path targetDir = basePath.resolve(directory).normalize();
            Files.createDirectories(targetDir);

            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String storedName = UUID.randomUUID() + extension;
            Path targetFile = targetDir.resolve(storedName);

            try (InputStream is = file.getInputStream()) {
                Files.copy(is, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Retorna caminho relativo ao baseDir
            return basePath.relativize(targetFile).toString().replace("\\", "/");
        } catch (IOException ex) {
            throw new RuntimeException("Falha ao armazenar arquivo: " + file.getOriginalFilename(), ex);
        }
    }

    @Override
    public byte[] load(String path) {
        try {
            Path filePath = basePath.resolve(path).normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Falha ao carregar arquivo: " + path, ex);
        }
    }

    @Override
    public void delete(String path) {
        try {
            Path filePath = basePath.resolve(path).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.warn("Falha ao remover arquivo do filesystem: {}", path, ex);
        }
    }

    @Override
    public String getUrl(String path) {
        return "/api/storage/file/" + path;
    }
}
