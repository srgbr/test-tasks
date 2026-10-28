package com.bank.service;

import com.bank.common.ApiException;
import com.bank.dao.AccountRepository;
import com.bank.dao.UserRepository;
import com.bank.dao.entity.AccountEntity;
import com.bank.dto.TransferRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public void transfer(Long fromUserId, TransferRequest request) {
        Long toUserId = request.getToUserId();
        BigDecimal value = request.getValue();

        if (fromUserId.equals(toUserId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot transfer to yourself");
        }
        if (!userRepository.existsById(toUserId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Recipient not found");
        }
        
        Long first = Math.min(fromUserId, toUserId);
        Long second = Math.max(fromUserId, toUserId);
        AccountEntity firstAcc = accountRepository.findByUserIdForUpdate(first)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));
        AccountEntity secondAcc = accountRepository.findByUserIdForUpdate(second)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Account not found"));

        AccountEntity from = fromUserId.equals(first) ? firstAcc : secondAcc;
        AccountEntity to = fromUserId.equals(first) ? secondAcc : firstAcc;

        if (from.getBalance().compareTo(value) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(value));
        to.setBalance(to.getBalance().add(value));
        log.info("Transfer {} from user {} to user {}", value, fromUserId, toUserId);
    }
}
