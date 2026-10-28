package com.bank.service;

import com.bank.common.ApiException;
import com.bank.dao.AccountRepository;
import com.bank.dao.UserRepository;
import com.bank.dao.entity.AccountEntity;
import com.bank.dto.TransferRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private TransferService transferService;

    @Test
    void transfersMoneyBetweenAccounts() {
        AccountEntity from = account(1L, "100.00");
        AccountEntity to = account(2L, "50.00");
        when(userRepository.existsById(2L)).thenReturn(true);
        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.of(to));

        transferService.transfer(1L, request(2L, "30.00"));

        assertEquals(new BigDecimal("70.00"), from.getBalance());
        assertEquals(new BigDecimal("80.00"), to.getBalance());
        verify(accountRepository).findByUserIdForUpdate(1L);
        verify(accountRepository).findByUserIdForUpdate(2L);
    }

    @Test
    void locksAccountsInStableOrder() {
        AccountEntity from = account(2L, "100.00");
        AccountEntity to = account(1L, "50.00");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(to));
        when(accountRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.of(from));

        transferService.transfer(2L, request(1L, "10.00"));

        assertEquals(new BigDecimal("90.00"), from.getBalance());
        assertEquals(new BigDecimal("60.00"), to.getBalance());
    }

    @Test
    void rejectsTransferToSelf() {
        ApiException ex = assertThrows(ApiException.class,
                () -> transferService.transfer(1L, request(1L, "10.00")));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void rejectsInsufficientFunds() {
        AccountEntity from = account(1L, "5.00");
        AccountEntity to = account(2L, "50.00");
        when(userRepository.existsById(2L)).thenReturn(true);
        when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.of(to));

        ApiException ex = assertThrows(ApiException.class,
                () -> transferService.transfer(1L, request(2L, "10.00")));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals(new BigDecimal("5.00"), from.getBalance());
        assertEquals(new BigDecimal("50.00"), to.getBalance());
    }

    @Test
    void rejectsUnknownRecipient() {
        when(userRepository.existsById(99L)).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class,
                () -> transferService.transfer(1L, request(99L, "10.00")));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    private static TransferRequest request(Long toUserId, String value) {
        TransferRequest request = new TransferRequest();
        request.setToUserId(toUserId);
        request.setValue(new BigDecimal(value));
        return request;
    }

    private static AccountEntity account(Long userId, String balance) {
        AccountEntity entity = new AccountEntity();
        entity.setUserId(userId);
        entity.setBalance(new BigDecimal(balance));
        entity.setInitialBalance(new BigDecimal(balance));
        return entity;
    }
}
