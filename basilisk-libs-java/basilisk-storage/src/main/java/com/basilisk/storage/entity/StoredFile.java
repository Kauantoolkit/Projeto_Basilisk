package com.basilisk.storage.entity;

import com.basilisk.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Metadados de um arquivo armazenado.
 * Pode ser vinculado a qualquer entidade via entityType + entityId.
 */
@Entity
@Table(name = "stored_files", indexes = {
        @Index(name = "idx_stored_files_entity", columnList = "entityType, entityId"),
        @Index(name = "idx_stored_files_uploaded_by", columnList = "uploadedBy")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredFile extends BaseEntity {

    @Column(nullable = false, length = 256)
    private String originalName;

    @Column(nullable = false, length = 512)
    private String storedPath;

    @Column(nullable = false, length = 128)
    private String contentType;

    @Column(nullable = false)
    private long size;

    /** Usuário que fez o upload (opcional). */
    @Column
    private UUID uploadedBy;

    /** Visibilidade do arquivo. Padrão: PRIVATE. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private Visibility visibility = Visibility.PRIVATE;

    /** Tipo da entidade associada (ex: "product", "user"). Opcional. */
    @Column(length = 100)
    private String entityType;

    /** ID da entidade associada. Opcional. */
    @Column(length = 100)
    private String entityId;

    public enum Visibility {
        PUBLIC, PRIVATE, TENANT
    }
}
