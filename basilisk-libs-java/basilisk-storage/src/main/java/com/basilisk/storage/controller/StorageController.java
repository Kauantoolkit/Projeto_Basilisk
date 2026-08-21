package com.basilisk.storage.controller;

import com.basilisk.storage.entity.StoredFile;
import com.basilisk.storage.entity.StoredFile.Visibility;
import com.basilisk.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints REST para upload, download e gerenciamento de arquivos.
 *
 * POST   /api/storage/upload                          - upload de arquivo
 * GET    /api/storage/{id}/download                   - download de arquivo
 * DELETE /api/storage/{id}                            - remoção de arquivo
 * GET    /api/storage/entity/{entityType}/{entityId}  - listagem por entidade
 * GET    /api/storage/user/{uploadedBy}               - listagem por usuário
 */
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public StoredFile upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "general") String directory,
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "entityId", required = false) String entityId,
            @RequestParam(value = "uploadedBy", required = false) UUID uploadedBy,
            @RequestParam(value = "visibility", defaultValue = "PRIVATE") Visibility visibility) {
        return storageService.upload(file, directory, entityType, entityId, uploadedBy, visibility);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        StoredFile storedFile = storageService.findById(id);
        byte[] content = storageService.download(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storedFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + storedFile.getOriginalName() + "\"")
                .body(content);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        storageService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public List<StoredFile> findByEntity(
            @PathVariable String entityType,
            @PathVariable String entityId) {
        return storageService.findByEntity(entityType, entityId);
    }

    @GetMapping("/user/{uploadedBy}")
    public List<StoredFile> findByUploadedBy(@PathVariable UUID uploadedBy) {
        return storageService.findByUploadedBy(uploadedBy);
    }
}
