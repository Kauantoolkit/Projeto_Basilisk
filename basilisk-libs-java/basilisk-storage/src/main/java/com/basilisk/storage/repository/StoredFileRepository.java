package com.basilisk.storage.repository;

import com.basilisk.storage.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {

    List<StoredFile> findByEntityTypeAndEntityId(String entityType, String entityId);
}
