package com.basilisk.storage.service;

import com.basilisk.storage.entity.StoredFile;
import com.basilisk.storage.provider.StorageProvider;
import com.basilisk.storage.repository.StoredFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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

    /**
     * Faz upload de um arquivo sem vínculo a entidade.
     */
    public StoredFile upload(MultipartFile file, String directory) {
        return upload(file, directory, null, null);
    }

    /**
     * Faz upload de um arquivo vinculado a uma entidade.
     */
    public StoredFile upload(MultipartFile file, String directory, String entityType, String entityId) {
        String storedPath = storageProvider.store(file, directory);

        StoredFile storedFile = StoredFile.builder()
                .originalName(file.getOriginalFilename())
                .storedPath(storedPath)
                .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .size(file.getSize())
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
     * Busca o registro pelo ID (UUID).
     */
    public StoredFile findById(String id) {
        return repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado: " + id));
    }
}
