package com.basilisk.storage.service;

import com.basilisk.core.exception.BusinessException;
import com.basilisk.storage.entity.StoredFile;
import com.basilisk.storage.entity.StoredFile.Visibility;
import com.basilisk.storage.properties.StorageProperties;
import com.basilisk.storage.provider.StorageProvider;
import com.basilisk.storage.repository.StoredFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço de alto nível para upload, download e remoção de arquivos.
 * Delega o armazenamento físico ao StorageProvider ativo e persiste
 * metadados na tabela stored_files.
 */
@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageProvider storageProvider;
    private final StoredFileRepository repository;
    private final StorageProperties properties;

    /**
     * Faz upload de um arquivo sem vínculo a entidade.
     */
    public StoredFile upload(MultipartFile file, String directory) {
        return upload(file, directory, null, null, null, Visibility.PRIVATE);
    }

    /**
     * Faz upload de um arquivo vinculado a uma entidade.
     */
    public StoredFile upload(MultipartFile file, String directory, String entityType, String entityId) {
        return upload(file, directory, entityType, entityId, null, Visibility.PRIVATE);
    }

    /**
     * Faz upload de um arquivo com controle de visibilidade e rastreamento de usuário.
     */
    public StoredFile upload(MultipartFile file, String directory, String entityType, String entityId,
                             UUID uploadedBy, Visibility visibility) {
        validateFile(file);

        String storedPath = storageProvider.store(file, directory);

        StoredFile storedFile = StoredFile.builder()
                .originalName(file.getOriginalFilename())
                .storedPath(storedPath)
                .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .size(file.getSize())
                .uploadedBy(uploadedBy)
                .visibility(visibility)
                .entityType(entityType)
                .entityId(entityId)
                .build();

        return repository.save(storedFile);
    }

    /**
     * Carrega o conteúdo do arquivo pelo ID do registro.
     */
    public byte[] download(String id) {
        StoredFile storedFile = findById(id);
        return storageProvider.load(storedFile.getStoredPath());
    }

    /**
     * Remove o arquivo do storage e o registro do banco.
     */
    public void remove(String id) {
        StoredFile storedFile = findById(id);
        storageProvider.delete(storedFile.getStoredPath());
        repository.delete(storedFile);
    }

    /**
     * Lista arquivos vinculados a uma entidade.
     */
    public List<StoredFile> findByEntity(String entityType, String entityId) {
        return repository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    /**
     * Lista arquivos enviados por um usuário.
     */
    public List<StoredFile> findByUploadedBy(UUID uploadedBy) {
        return repository.findByUploadedBy(uploadedBy);
    }

    /**
     * Busca o registro pelo ID (UUID).
     */
    public StoredFile findById(String id) {
        return repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BusinessException("Arquivo não encontrado: " + id, HttpStatus.NOT_FOUND));
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Arquivo vazio");
        }

        if (file.getSize() > properties.getMaxFileSize()) {
            throw new BusinessException("Arquivo excede o tamanho máximo de " +
                    (properties.getMaxFileSize() / 1024 / 1024) + "MB");
        }

        Set<String> allowed = Arrays.stream(properties.getAllowedTypes().split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        if (file.getContentType() != null && !allowed.contains(file.getContentType())) {
            throw new BusinessException("Tipo de arquivo não permitido: " + file.getContentType());
        }
    }
}
