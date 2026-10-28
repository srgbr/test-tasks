package com.bank.dao;

import com.bank.dao.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a where a.userId = :userId")
    Optional<AccountEntity> findByUserIdForUpdate(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a order by a.userId")
    List<AccountEntity> findAllForUpdate();
}
