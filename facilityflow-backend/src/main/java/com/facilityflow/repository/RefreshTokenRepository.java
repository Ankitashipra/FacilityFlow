package com.facilityflow.repository;

import com.facilityflow.entity.RefreshToken;
import com.facilityflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.user = :user")
    void revokeAllByUser(User user);

    @Modifying
    @Query("delete from RefreshToken r where r.expiryDate < CURRENT_TIMESTAMP")
    void deleteAllExpired();
}
