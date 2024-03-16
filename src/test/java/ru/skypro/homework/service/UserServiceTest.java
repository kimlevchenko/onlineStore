package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.PasswordsNotEqualsException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(WebSecurityConfig.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @Spy
    private UserMapper userMapper;

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
    public void setPasswordTest() {
        NewPasswordDto newPasswordDto = new NewPasswordDto();
        newPasswordDto.setCurrentPassword("password");
        newPasswordDto.setNewPassword("newPassword");
        String username = "username";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(encoder.matches("password", "encodedPassword")).thenReturn(true);
        when(encoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userService.setPassword(newPasswordDto, principal);

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void setPasswordUserNotFoundTest() {
        NewPasswordDto newPasswordDto = new NewPasswordDto();
        newPasswordDto.setCurrentPassword("password");
        newPasswordDto.setNewPassword("newPassword");
        String username = "username";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.setPassword(newPasswordDto, principal));

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(any());
    }
    @Test
    public void setPasswordPasswordsNotEqualsTest() {
        NewPasswordDto newPasswordDto = new NewPasswordDto();
        newPasswordDto.setCurrentPassword("errorPassword");
        newPasswordDto.setNewPassword("newPassword");
        String username = "username";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(encoder.matches("errorPassword", "encodedPassword")).thenReturn(false);

        assertThatExceptionOfType(PasswordsNotEqualsException.class)
                .isThrownBy(() -> userService.setPassword(newPasswordDto, principal));

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(any());
    }
    @Test
    public void getInfoAboutAuthorizedUserTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setEmail("username");
        userDto.setFirstName("Ivan");
        userDto.setLastName("Ivanov");
        userDto.setPhone("+7852-123-45-67");
        userDto.setRole(Role.USER);
        userDto.setImage("/users/me/image");

        String username = "username";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThat(userService.getInfoAboutAuthorizedUser(principal)).isEqualTo(userDto);
        verify(userRepository, times(1)).findByUsername(username);
    }
    @Test
    public void getInfoAboutAuthorizedUserUserNotFoundTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setEmail("username");
        userDto.setFirstName("Ivan");
        userDto.setLastName("Ivanov");
        userDto.setPhone("+7852-123-45-67");
        userDto.setRole(Role.USER);
        userDto.setImage("/users/me/image");

        String username = "username";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.getInfoAboutAuthorizedUser(principal));
        verify(userRepository, times(1)).findByUsername(username);
    }
    @Test
    public void setInfoAboutAuthorizedUserTest() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("Petr");
        updateUserDto.setLastName("Petrov");
        updateUserDto.setPhone("+7985-111-11-22");

        String username = "username";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThat(userService.setInfoAboutAuthorizedUser(updateUserDto, principal))
                .isEqualTo(updateUserDto);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void setInfoAboutAuthorizedUserUserNotFoundTest() {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("Petr");
        updateUserDto.setLastName("Petrov");
        updateUserDto.setPhone("+7985-111-11-22");

        String username = "username";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.setInfoAboutAuthorizedUser(updateUserDto, principal));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(user);
    }
    @Test
    public void setAvatarTest() throws IOException {
        String username = "username";
        byte[] imageData = "testImage".getBytes();
        MultipartFile image = mock(MultipartFile.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(image.getBytes()).thenReturn(imageData);

        userService.setAvatar(image, principal);

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void setAvatarUserNotFoundTest() throws IOException {
        String username = "username";
        byte[] imageData = "testImage".getBytes();
        MultipartFile image = mock(MultipartFile.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.setAvatar(image, principal));

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(user);
    }
    @Test
    public void setAvatarIOExceptionTest() throws IOException {
        String username = "username";
        MultipartFile image = mock(MultipartFile.class);
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(image.getBytes()).thenThrow(new IOException());
        assertThrows(RuntimeException.class, () -> userService.setAvatar(image, principal));

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(user);
    }

    @Test
    public void getAvatarTest() {
        String username = "username";
        byte[] imageData = new byte[]{1, 2, 3};
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThat(userService.getAvatar(principal)).isEqualTo(imageData);
        verify(userRepository, times(1)).findByUsername(username);
    }
    @Test
    public void getAvatarUserNotFoundTest() {
        String username = "username";
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.getAvatar(principal));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void getAvatarByIdTest() {
        int id = 1;
        byte[] imageData = new byte[]{1, 2, 3};
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThat(userService.getAvatarById(id)).isEqualTo(imageData);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    public void getAvatarByIdUserNotFoundTest() {
        int id = 1;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.getAvatarById(id));
        verify(userRepository, times(1)).findById(id);
    }
}
