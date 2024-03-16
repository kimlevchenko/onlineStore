package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.PasswordsNotEqualsException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    /**
     * Метод для смены пароля пользователя.
     * Кодировка нового пароля пользователя с помощью бина PasswordEncoder.
     *
     * @param newPasswordDto Dto NewPasswordDto.
     * @param principal      интерфейс для получения username пользователя.
     * @throws UserNotFoundException       выбрасывается, если пользователь не найден в таблице user.
     * @throws PasswordsNotEqualsException выбрасывается, если текущий пароль в NewPasswordDto
     *                                     не совпадает с паролем в таблице user.
     */
    public void setPassword(NewPasswordDto newPasswordDto, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        if (!encoder.matches(newPasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new PasswordsNotEqualsException();
        }
        user.setPassword(encoder.encode(newPasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Метод для получения информации об аутентифицированном пользователе.
     *
     * @param principal интерфейс для получения username пользователя.
     * @return Dto UserDto.
     * @throws UserNotFoundException выбрасывается, если пользователь не найден в таблице user.
     */
    public UserDto getInfoAboutAuthorizedUser(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return userMapper.userToUserDto(user);
    }

    /**
     * Метод для изменения информации аутентифицированного пользователя.
     *
     * @param updateUserDto Dto UpdateUserDto.
     * @param principal     интерфейс для получения username пользователя.
     * @return Dto UpdateUserDto.
     * @throws UserNotFoundException выбрасывается, если пользователь не найден в таблице user.
     */
    public UpdateUserDto setInfoAboutAuthorizedUser(UpdateUserDto updateUserDto, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setPhone(updateUserDto.getPhone());
        userRepository.save(user);
        return updateUserDto;
    }

    /**
     * Метод для изменения аватарки пользователя.
     *
     * @param image     картинка с аватаркой.
     * @param principal интерфейс для получения username пользователя.
     * @throws UserNotFoundException выбрасывается, если пользователь не найден в таблице user.
     * @throws IOException           выбрасывается, если возникают проблемы при получении картинки.
     */
    @Transactional
    public void setAvatar(MultipartFile image, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        try {
            user.setImage(image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        userRepository.save(user);
    }

    /**
     * Метод для получения аватарки пользователя из таблицы user.
     *
     * @param principal интерфейс для получения username пользователя.
     * @return аватарка в виде массива байтов.
     * @throws UserNotFoundException выбрасывается, если пользователь не найден в таблице user.
     */
    public byte[] getAvatar(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username)).getImage();
    }

    /**
     * Метод для получения аватарки пользователя из таблицы user по id пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return аватарка в виде массива байтов.
     * @throws UserNotFoundException выбрасывается, если пользователь не найден в таблице user.
     */
    public byte[] getAvatarById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User by Id " + userId)).getImage();
    }
}
