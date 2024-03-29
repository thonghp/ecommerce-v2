package com.hpt.backend.user;

import com.hpt.backend.paging.SearchRepository;
import com.hpt.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends SearchRepository<User, Integer> {
    User findByEmail(String email);

    Long countById(Integer id);

    @Query("UPDATE User SET enabled = ?2 WHERE id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);

    @Query("SELECT u FROM User u WHERE CONCAT(u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
    Page<User> findAll(String keyword, Pageable pageable);
}
