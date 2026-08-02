package com.facilityflow.repository;

import com.facilityflow.entity.Role;
import com.facilityflow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("select count(u) from User u where u.enabled = true")
    long countActiveUsers();

    @Query("select count(u) from User u")
    long countAllUsers();

    @Query("select u from User u where lower(u.fullName) like lower(concat('%', :term, '%')) " +
           "or lower(u.email) like lower(concat('%', :term, '%'))")
    Page<User> search(@Param("term") String term, Pageable pageable);
}
