package com.bank.service;

import com.bank.dao.AccountRepository;
import com.bank.dao.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceScheduler {

    private static final BigDecimal RATE = new BigDecimal("1.10");
    private static final BigDecimal CAP = new BigDecimal("2.07");

    private final AccountRepository accountRepository;

    @Scheduled(fixedRate = 30_000)
    @Transactional
    public void accrue() {
        List<AccountEntity> accounts = accountRepository.findAll();
        int updated = 0;
        for (AccountEntity account : accounts) {
            BigDecimal max = account.getInitialBalance().multiply(CAP).setScale(2, RoundingMode.HALF_UP);
            if (account.getBalance().compareTo(max) >= 0) {
                continue;
            }
            BigDecimal next = account.getBalance().multiply(RATE).setScale(2, RoundingMode.HALF_UP);
            if (next.compareTo(max) > 0) {
                next = max;
            }
            account.setBalance(next);
            updated++;
        }
        if (updated > 0) {
            log.info("Balance accrual updated {} accounts", updated);
        }
    }
}
