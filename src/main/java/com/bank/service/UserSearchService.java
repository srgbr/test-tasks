package com.bank.service;

import com.bank.dao.EmailDataRepository;
import com.bank.dao.PhoneDataRepository;
import com.bank.dao.UserRepository;
import com.bank.dao.UserSpecifications;
import com.bank.dao.entity.EmailDataEntity;
import com.bank.dao.entity.PhoneDataEntity;
import com.bank.dao.entity.UserEntity;
import com.bank.dto.PageResponse;
import com.bank.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "users")
    public PageResponse<UserResponse> search(LocalDate dateOfBirth, String phone, String name, String email,
                                             Pageable pageable) {
        log.info("Search users dateOfBirth={}, phone={}, name={}, email={}, page={}",
                dateOfBirth, phone, name, email, pageable);
        Page<UserEntity> page = userRepository.findAll(
                UserSpecifications.filter(dateOfBirth, phone, name, email), pageable);
        List<Long> ids = page.getContent().stream().map(UserEntity::getId).collect(Collectors.toList());
        Map<Long, List<String>> emails = emailsByUser(ids);
        Map<Long, List<String>> phones = phonesByUser(ids);
        List<UserResponse> content = page.getContent().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getDateOfBirth(),
                        emails.getOrDefault(user.getId(), Collections.emptyList()),
                        phones.getOrDefault(user.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
        return PageResponse.of(page, content);
    }

    private Map<Long, List<String>> emailsByUser(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return emailDataRepository.findByUserIdIn(userIds).stream()
                .collect(Collectors.groupingBy(EmailDataEntity::getUserId,
                        Collectors.mapping(EmailDataEntity::getEmail, Collectors.toList())));
    }

    private Map<Long, List<String>> phonesByUser(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return phoneDataRepository.findByUserIdIn(userIds).stream()
                .collect(Collectors.groupingBy(PhoneDataEntity::getUserId,
                        Collectors.mapping(PhoneDataEntity::getPhone, Collectors.toList())));
    }
}
