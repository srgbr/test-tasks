package com.bank.dao;

import com.bank.dao.entity.PhoneDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneDataRepository extends JpaRepository<PhoneDataEntity, Long> {

    Optional<PhoneDataEntity> findByPhone(String phone);
}
