package com.basilisk.api.permission.entity;

import com.basilisk.api.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "resource"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String resource;

    @Column(nullable = false)
    @Builder.Default
    private Long value = 0L;

    public boolean hasPermission(long bitmask) {
        return (this.value & bitmask) == bitmask;
    }

    public void grantPermission(long bitmask) {
        this.value = this.value | bitmask;
    }

    public void revokePermission(long bitmask) {
        this.value = this.value & ~bitmask;
    }
}
