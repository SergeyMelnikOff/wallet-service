package com.example.wallet.controller;

import com.example.wallet.dto.WalletOperationRequest;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class WalletController {
    
    private final WalletService walletService;
    
    @PostMapping("/wallet")
    public ResponseEntity<WalletResponse> processOperation(
            @Valid @RequestBody WalletOperationRequest request) {
        
        log.info("Processing operation for wallet: {}, type: {}, amount: {}",
                request.getWalletId(), request.getOperationType(), request.getAmount());
        
        WalletResponse response = walletService.processOperation(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<WalletResponse> getWalletBalance(
            @PathVariable UUID walletId) {
        
        log.info("Getting balance for wallet: {}", walletId);
        
        WalletResponse response = walletService.getWalletBalance(walletId);
        return ResponseEntity.ok(response);
    }
}