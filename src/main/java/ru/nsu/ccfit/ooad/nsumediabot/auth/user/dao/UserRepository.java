package ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.chatId = :chatId")
    Optional<User> findByChatId(@Param("chatId") Long chatId);

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' ORDER BY u.chatId")
    List<User> findAllAdmins();

    @Query("SELECT COUNT (u) > 0 FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.chatId = :chatId")
    void deleteByChatId(@Param("chatId") Long chatId);
}