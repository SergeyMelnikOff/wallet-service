package com.example.wallet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Data
public class Wallet {
    
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    
    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}