package com.basilisk.storage.entity;

import com.basilisk.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Metadados de um arquivo armazenado.
 * Pode ser vinculado a qualquer entidade via entityType + entityId.
 */
@Entity
@Table(name = "stored_files", indexes = {
        @Index(name = "idx_stored_files_entity", columnList = "entityType, entityId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredFile extends BaseEntity {

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedPath;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(length = 100)
    private String entityType;

    @Column(length = 100)
    private String entityId;
}
