package com.bank.service;

import com.bank.common.ApiException;
import com.bank.dao.EmailDataRepository;
import com.bank.dao.PhoneDataRepository;
import com.bank.dao.entity.EmailDataEntity;
import com.bank.dao.entity.PhoneDataEntity;
import com.bank.dto.ContactResponse;
import com.bank.dto.EmailRequest;
import com.bank.dto.PhoneRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserContactService {

    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public ContactResponse addEmail(Long userId, EmailRequest request) {
        ensureEmailFree(request.getEmail());
        EmailDataEntity entity = new EmailDataEntity();
        entity.setUserId(userId);
        entity.setEmail(request.getEmail());
        emailDataRepository.save(entity);
        log.info("User {} added email id={}", userId, entity.getId());
        return new ContactResponse(entity.getId(), entity.getEmail());
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public ContactResponse updateEmail(Long userId, Long emailId, EmailRequest request) {
        EmailDataEntity entity = requireOwnEmail(userId, emailId);
        if (!entity.getEmail().equals(request.getEmail())) {
            ensureEmailFree(request.getEmail());
            entity.setEmail(request.getEmail());
        }
        log.info("User {} updated email id={}", userId, emailId);
        return new ContactResponse(entity.getId(), entity.getEmail());
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void deleteEmail(Long userId, Long emailId) {
        requireOwnEmail(userId, emailId);
        if (emailDataRepository.countByUserId(userId) <= 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot delete the last email");
        }
        emailDataRepository.deleteById(emailId);
        log.info("User {} deleted email id={}", userId, emailId);
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public ContactResponse addPhone(Long userId, PhoneRequest request) {
        ensurePhoneFree(request.getPhone());
        PhoneDataEntity entity = new PhoneDataEntity();
        entity.setUserId(userId);
        entity.setPhone(request.getPhone());
        phoneDataRepository.save(entity);
        log.info("User {} added phone id={}", userId, entity.getId());
        return new ContactResponse(entity.getId(), entity.getPhone());
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public ContactResponse updatePhone(Long userId, Long phoneId, PhoneRequest request) {
        PhoneDataEntity entity = requireOwnPhone(userId, phoneId);
        if (!entity.getPhone().equals(request.getPhone())) {
            ensurePhoneFree(request.getPhone());
            entity.setPhone(request.getPhone());
        }
        log.info("User {} updated phone id={}", userId, phoneId);
        return new ContactResponse(entity.getId(), entity.getPhone());
    }

    @Transactional
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void deletePhone(Long userId, Long phoneId) {
        requireOwnPhone(userId, phoneId);
        if (phoneDataRepository.countByUserId(userId) <= 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot delete the last phone");
        }
        phoneDataRepository.deleteById(phoneId);
        log.info("User {} deleted phone id={}", userId, phoneId);
    }

    private EmailDataEntity requireOwnEmail(Long userId, Long emailId) {
        EmailDataEntity entity = emailDataRepository.findById(emailId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Email not found"));
        if (!entity.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your email");
        }
        return entity;
    }

    private PhoneDataEntity requireOwnPhone(Long userId, Long phoneId) {
        PhoneDataEntity entity = phoneDataRepository.findById(phoneId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Phone not found"));
        if (!entity.getUserId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your phone");
        }
        return entity;
    }

    private void ensureEmailFree(String email) {
        if (emailDataRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already taken");
        }
    }

    private void ensurePhoneFree(String phone) {
        if (phoneDataRepository.existsByPhone(phone)) {
            throw new ApiException(HttpStatus.CONFLICT, "Phone already taken");
        }
    }
}
