package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;

@Component
public class UserMapper {

    /**
     * Метод преобразует Dto Register в объект класса User.
     *
     * @param register Dto Register.
     * @return объект класса User.
     */
    public User registerToUser(Register register) {
        User newUser = new User();
        newUser.setUsername(register.getUsername());
        newUser.setPassword(register.getPassword());
        newUser.setFirstName(register.getFirstName());
        newUser.setLastName(register.getLastName());
        newUser.setPhone(register.getPhone());
        newUser.setRole(register.getRole());
        return newUser;
    }

    /**
     * Метод преобразует объект класса User в Dto UserDto.
     *
     * @param user объект класса User.
     * @return Dto UserDto.
     */
    public UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhone(user.getPhone());
        userDto.setRole(user.getRole());
        userDto.setImage("/users/me/image");
        return userDto;
    }


}
