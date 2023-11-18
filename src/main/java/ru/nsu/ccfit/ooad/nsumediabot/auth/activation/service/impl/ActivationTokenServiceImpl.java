package ru.nsu.ccfit.ooad.nsumediabot.auth.activation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ccfit.ooad.nsumediabot.auth.activation.dao.ActivationToken;
import ru.nsu.ccfit.ooad.nsumediabot.auth.activation.dao.ActivationTokenRepository;
import ru.nsu.ccfit.ooad.nsumediabot.auth.activation.exception.ActivationTokenNotFoundException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.activation.service.ActivationTokenService;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.User;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.UserRepository;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dto.UserDto;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.exception.UserNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivationTokenServiceImpl
        implements ActivationTokenService {

    private final ModelMapper modelMapper;
    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;

    @Value("${activation.token.time-to-live}")
    private long timeToLive;

    @Override
    @Transactional
    public String createToken(UserDto userDto) {
        User user = userRepository.findByChatId(userDto.getChatId())
                .orElse(null);
        if (user == null) {
            log.error("User with chat ID {} not found", userDto.getChatId());
            throw new UserNotFoundException();
        }

        ActivationToken activationToken = activationTokenRepository.findByUser(user)
                .orElse(new ActivationToken());
        activationToken.setUser(user);
        activationToken.setToken(UUID.randomUUID().toString());
        activationToken.setExpiredTime(new Date(System.currentTimeMillis() + timeToLive));

        activationTokenRepository.save(activationToken);
        return activationToken.getToken();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getChatIdByToken(String token) {
        ActivationToken activationToken = activationTokenRepository.findNotExpiredByToken(token)
                .orElse(null);
        if (activationToken == null) {
            log.error("Activation token {} not found or expired", token);
            throw new ActivationTokenNotFoundException();
        }
        return activationToken.getUser().getChatId();
    }

    @Override
    @Transactional
    public void deleteToken(String token) {
        activationTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        List<ActivationToken> expiredTokens = activationTokenRepository.findAllExpired();
        expiredTokens.forEach(activationToken -> userRepository.deleteByChatId(activationToken.getId()));
        activationTokenRepository.deleteAllExpired();
    }
}
