package com.bank.dao;

import com.bank.dao.entity.EmailDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailDataRepository extends JpaRepository<EmailDataEntity, Long> {

    Optional<EmailDataEntity> findByEmail(String email);
}
