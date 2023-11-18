package ru.nsu.ccfit.ooad.nsumediabot.auth.activation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.User;

import java.util.List;
import java.util.Optional;

public interface ActivationTokenRepository
        extends JpaRepository<ActivationToken, Long> {

    @Query("SELECT a FROM ActivationToken a WHERE a.user = :user")
    Optional<ActivationToken> findByUser(@Param("user") User user);

    @Query("SELECT a FROM ActivationToken a WHERE a.token = :token AND a.expiredTime > CURRENT_TIMESTAMP")
    Optional<ActivationToken> findNotExpiredByToken(@Param("token") String token);

    @Query("SELECT a FROM ActivationToken a WHERE a.expiredTime < CURRENT_TIMESTAMP")
    List<ActivationToken> findAllExpired();

    @Modifying
    @Query("DELETE FROM ActivationToken a WHERE a.expiredTime < CURRENT_TIMESTAMP")
    void deleteAllExpired();

    @Modifying
    @Query("DELETE FROM ActivationToken a WHERE a.token = :token")
    void deleteByToken(@Param("token") String token);
}