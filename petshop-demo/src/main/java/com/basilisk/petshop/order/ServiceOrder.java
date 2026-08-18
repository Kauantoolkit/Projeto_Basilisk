package com.basilisk.petshop.order;

import com.basilisk.petshop.customer.Customer;
import com.basilisk.petshop.pet.Pet;
import com.basilisk.status.annotation.StatusConfig;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@StatusConfig(
        states = {"scheduled", "in_progress", "completed", "cancelled"},
        initialState = "scheduled",
        transitions = {
                "scheduled->in_progress",
                "scheduled->cancelled",
                "in_progress->completed",
                "in_progress->cancelled"
        }
)
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false)
    private String type;

    private String description;

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    private LocalDateTime completedDate;

    @Builder.Default
    private String status = "scheduled";

    @Column(nullable = false)
    private BigDecimal price;

    @Builder.Default
    private boolean active = true;
}
