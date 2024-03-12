package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceConfig implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Метод для поиска пользователя в таблице user по username.
     *
     * @param username username пользователя.
     * @return Optional объект класса User.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Метод для аутентификации пользователя в Spring Security. Поиск пользователя в таблице user
     * с помощью метода {@link UserServiceConfig#findByUsername(String)}. Преобразование объекта
     * класса User в объект класса UserDetails.
     *
     * @param username username пользователя.
     * @return объект класса UserDetails.
     * @throws UsernameNotFoundException если пользователь не найден.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}