package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.UserServiceConfig;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserServiceConfig userServiceConfig;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserServiceConfig userServiceConfig,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           UserMapper userMapper) {
        this.userServiceConfig = userServiceConfig;
        this.encoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Метод для логирования.
     * @param userName username пользователя.
     * @param password пароль пользователя.
     * @return true, если логирование прошло успешно, в противном случае возвращается false.
     */
    @Override
    public boolean login(String userName, String password) {
        if (userServiceConfig.findByUsername(userName).isEmpty()) {
            return false;
        }
        UserDetails userDetails = userServiceConfig.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }

    /**
     * Метод для регистрации нового пользователя, пользователь сохраняется в таблице user.
     * Для проверки наличия пользователя в таблице user используется
     * метод {@link UserServiceConfig#findByUsername(String)}.
     * Происходит кодировка пароля пользователя с помощью бина PasswordEncoder.
     * @param register Dto Register.
     * @return true, если пользователь отсутствует в таблице user, в противном случае возвращается false.
     */
    @Override
    public boolean register(Register register) {
        if (userServiceConfig.findByUsername(register.getUsername()).isPresent()) {
            return false;
        }
        register.setPassword(encoder.encode(register.getPassword()));
        userRepository.save(userMapper.registerToUser(register));
        return true;
    }


}
