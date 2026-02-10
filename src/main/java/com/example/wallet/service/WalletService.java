package com.example.wallet.service;

import com.example.wallet.dto.WalletOperationRequest;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.exception.InsufficientFundsException;
import com.example.wallet.exception.WalletNotFoundException;
import com.example.wallet.model.OperationType;
import com.example.wallet.model.Wallet;
import com.example.wallet.model.WalletTransaction;
import com.example.wallet.repository.WalletRepository;
import com.example.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(
        retryFor = {OptimisticLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 100)
    )
    public WalletResponse processOperation(WalletOperationRequest request) {
        Wallet wallet = walletRepository.findWithLockingById(request.getWalletId())
            .orElseGet(() -> createWallet(request.getWalletId()));
        
        BigDecimal newBalance;
        
        if (request.getOperationType() == OperationType.DEPOSIT) {
            newBalance = wallet.getBalance().add(request.getAmount());
        } else { // WITHDRAW
            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientFundsException(
                    "Insufficient funds. Current balance: " + wallet.getBalance()
                );
            }
            newBalance = wallet.getBalance().subtract(request.getAmount());
        }
        
        wallet.setBalance(newBalance);
        Wallet savedWallet = walletRepository.save(wallet);
        
        // Сохраняем транзакцию
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWalletId(wallet.getId());
        transaction.setOperationType(request.getOperationType());
        transaction.setAmount(request.getAmount());
        transactionRepository.save(transaction);
        
        return mapToResponse(savedWallet);
    }
    
    @Transactional(readOnly = true)
    public WalletResponse getWalletBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + walletId));
        
        return mapToResponse(wallet);
    }
    
    private Wallet createWallet(UUID walletId) {
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.ZERO);
        return walletRepository.save(wallet);
    }
    
    private WalletResponse mapToResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setBalance(wallet.getBalance());
        return response;
    }
}