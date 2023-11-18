package ru.nsu.ccfit.ooad.nsumediabot.auth.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.Role;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.User;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dao.UserRepository;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.dto.UserDto;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.exception.UserAlreadyExistsException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.exception.UserNotFoundException;
import ru.nsu.ccfit.ooad.nsumediabot.auth.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl
        implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDto loadUserByChatId(Long chatId) {
        User user = userRepository.findByChatId(chatId).orElse(null);
        if (user == null) {
            log.error("User with chat ID {} not found", chatId);
            throw new UserNotFoundException();
        }
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public void addUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.error("User {} not found", userDto.getEmail());
            throw new UserAlreadyExistsException();
        }
        User user = modelMapper.map(userDto, User.class);
        userRepository.save(user);
        log.info("User {} added", userDto.getEmail());
    }

    @Override
    @Transactional
    public void changeRoleByChatId(Long chatId, Role role) {
        User user = userRepository.findByChatId(chatId).orElse(null);
        if (user == null) {
            log.error("User with chat ID {} not found", chatId);
            throw new UserNotFoundException();
        }
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> loadAllAdmins() {
        return userRepository.findAllAdmins()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }
}
