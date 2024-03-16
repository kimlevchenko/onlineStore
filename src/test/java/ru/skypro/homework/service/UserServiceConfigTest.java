package ru.skypro.homework.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceConfigTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceConfig userServiceConfig;

    private User user;

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
    }

    @Test
    public void findByUsernameTest() {
        String username = user.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThat(userServiceConfig.findByUsername(username)).isEqualTo(Optional.of(user));
        verify(userRepository, times(1)).findByUsername(username);
    }
    @Test
    public void loadUserByUsernameTest() {
        String username = user.getUsername();
        org.springframework.security.core.userdetails.User newUser =
                new org.springframework.security.core.userdetails.User(
                        "username",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                );
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThat(userServiceConfig.loadUserByUsername(username)).isEqualTo(newUser);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void loadUserByUsernameUserNotFoundTest() {
        String username = user.getUsername();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userServiceConfig.loadUserByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

}
