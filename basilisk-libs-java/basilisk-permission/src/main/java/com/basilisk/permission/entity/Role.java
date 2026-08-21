package com.basilisk.permission.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(length = 256)
    private String description;

    /**
     * Roles de sistema (OWNER, ADMIN) não podem ser deletadas nem editadas pelo tenant.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean system = false;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<RolePermission> permissions = new HashSet<>();

    @CreationTimestamp
    private Instant createdAt;

    public boolean hasPermission(String resource, String action) {
        return permissions.stream().anyMatch(rp ->
                (rp.getResource().equals("*") || rp.getResource().equals(resource)) &&
                (rp.getAction().equals("*") || rp.getAction().equals(action))
        );
    }

    public void grantPermission(String resource, String action) {
        boolean exists = permissions.stream().anyMatch(rp ->
                rp.getResource().equals(resource) && rp.getAction().equals(action));
        if (!exists) {
            RolePermission rp = RolePermission.builder()
                    .role(this)
                    .resource(resource)
                    .action(action)
                    .build();
            permissions.add(rp);
        }
    }

    public void revokePermission(String resource, String action) {
        permissions.removeIf(rp ->
                rp.getResource().equals(resource) && rp.getAction().equals(action));
    }
}
