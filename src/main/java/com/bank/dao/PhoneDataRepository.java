package com.bank.dao;

import com.bank.dao.entity.PhoneDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhoneDataRepository extends JpaRepository<PhoneDataEntity, Long> {

    Optional<PhoneDataEntity> findByPhone(String phone);

    List<PhoneDataEntity> findByUserId(Long userId);

    long countByUserId(Long userId);

    boolean existsByPhone(String phone);
}
