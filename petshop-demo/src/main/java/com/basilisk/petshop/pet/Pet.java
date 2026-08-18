package com.basilisk.petshop.pet;

import com.basilisk.petshop.customer.Customer;
import com.basilisk.status.annotation.StatusConfig;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "pets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@StatusConfig(
        states = {"available", "reserved", "adopted", "in_treatment"},
        initialState = "available",
        transitions = {
                "available->reserved",
                "available->adopted",
                "available->in_treatment",
                "reserved->available",
                "reserved->adopted",
                "in_treatment->available",
                "in_treatment->adopted"
        }
)
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String species;

    private String breed;

    private Integer age;

    private Double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Customer owner;

    @Builder.Default
    private String status = "available";

    private String imageUrl;

    @Builder.Default
    private boolean active = true;
}
