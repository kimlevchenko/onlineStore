package ru.skypro.homework.mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @Spy
    private UserMapper userMapper;

    @Test
    public void registerToUserTest() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setPhone("+7852-123-45-67");
        user.setRole(Role.USER);

        Register register = new Register();
        register.setUsername("username");
        register.setPassword("password");
        register.setFirstName("Ivan");
        register.setLastName("Ivanov");
        register.setPhone("+7852-123-45-67");
        register.setRole(Role.USER);

        assertThat(userMapper.registerToUser(register)).isEqualTo(user);
    }
    @Test
    public void userToUserDtoTest() {
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setPassword("password");
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setPhone("+7852-123-45-67");
        user.setRole(Role.USER);

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setEmail("username");
        userDto.setFirstName("Ivan");
        userDto.setLastName("Ivanov");
        userDto.setPhone("+7852-123-45-67");
        userDto.setRole(Role.USER);
        userDto.setImage("/users/me/image");

        assertThat(userMapper.userToUserDto(user)).isEqualTo(userDto);
    }

}
