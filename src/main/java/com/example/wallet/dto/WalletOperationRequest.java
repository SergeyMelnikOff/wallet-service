package com.example.wallet.dto;

import com.example.wallet.model.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletOperationRequest {
    
    @NotNull(message = "walletId is required")
    private UUID walletId;
    
    @NotNull(message = "operationType is required")
    private OperationType operationType;
    
    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    private BigDecimal amount;
}