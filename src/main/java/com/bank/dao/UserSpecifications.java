package com.bank.dao;

import com.bank.dao.entity.EmailDataEntity;
import com.bank.dao.entity.PhoneDataEntity;
import com.bank.dao.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<UserEntity> filter(LocalDate dateOfBirth, String phone, String name, String email) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dateOfBirth != null) {
                predicates.add(cb.greaterThan(root.get("dateOfBirth"), dateOfBirth));
            }
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(root.get("name"), name + "%"));
            }
            if (StringUtils.hasText(email)) {
                Subquery<Long> emails = query.subquery(Long.class);
                Root<EmailDataEntity> emailRoot = emails.from(EmailDataEntity.class);
                emails.select(emailRoot.get("userId")).where(cb.equal(emailRoot.get("email"), email));
                predicates.add(root.get("id").in(emails));
            }
            if (StringUtils.hasText(phone)) {
                Subquery<Long> phones = query.subquery(Long.class);
                Root<PhoneDataEntity> phoneRoot = phones.from(PhoneDataEntity.class);
                phones.select(phoneRoot.get("userId")).where(cb.equal(phoneRoot.get("phone"), phone));
                predicates.add(root.get("id").in(phones));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
