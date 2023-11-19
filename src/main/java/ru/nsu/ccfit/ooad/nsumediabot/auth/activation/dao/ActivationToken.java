package ru.nsu.ccfit.ooad.nsumediabot.auth.activation.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.User;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "activation_tokens")
public class ActivationToken {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "expired_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredTime;

}