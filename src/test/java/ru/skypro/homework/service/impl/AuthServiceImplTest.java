package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserServiceConfig;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(WebSecurityConfig.class)
public class AuthServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserServiceConfig userServiceConfig;

    @InjectMocks
    private AuthServiceImpl authService;

    @Spy
    private UserMapper userMapper;

    private User user;

    private Register register;
    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setPassword("encodedPassword");
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setPhone("+7852-123-45-67");
        user.setRole(Role.USER);
        user.setImage(new byte[]{1, 2, 3});

        register = new Register();
        register.setUsername("username");
        register.setPassword("password");
        register.setFirstName("Ivan");
        register.setLastName("Ivanov");
        register.setPhone("+7852-123-45-67");
        register.setRole(Role.USER);
    }
    @Test
    void loginTest() {
        String username = "username";
        String password = "password";

        org.springframework.security.core.userdetails.User newUser
                = new org.springframework.security.core.userdetails.User(
                "username", "password", new ArrayList<>());
        when(userServiceConfig.findByUsername(username)).thenReturn(Optional.of(user));
        when(userServiceConfig.loadUserByUsername(username)).thenReturn(newUser);
        when(passwordEncoder.matches(password, newUser.getPassword())).thenReturn(true);

        assertThat(authService.login(username, password)).isTrue();

        verify(userServiceConfig, times(1)).findByUsername(username);
        verify(userServiceConfig, times(1)).loadUserByUsername(username);
    }

    @Test
    void loginFailedTest() {
        String username = "username";
        String password = "password";

        when(userServiceConfig.findByUsername(username)).thenReturn(Optional.empty());

        assertThat(authService.login(username, password)).isFalse();

        verify(userServiceConfig, times(1)).findByUsername(username);
        verify(userServiceConfig, never()).loadUserByUsername(username);
    }
    @Test
    void registerTest() {
        when(userServiceConfig.findByUsername(register.getUsername())).thenReturn(Optional.empty());

        assertThat(authService.register(register)).isTrue();

        verify(userServiceConfig, times(1)).findByUsername(register.getUsername());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testRegisterFailed() {
        when(userServiceConfig.findByUsername(register.getUsername())).thenReturn(Optional.of(user));

        assertThat(authService.register(register)).isFalse();

        verify(userServiceConfig, times(1)).findByUsername(register.getUsername());
        verify(userRepository, never()).save(any());
    }
}
