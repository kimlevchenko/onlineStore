package ru.skypro.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;

@RestController
@RequestMapping("/users")
@CrossOrigin(value = "http://localhost:3000")
public class UserController {

    @PostMapping("/set_password")
    public ResponseEntity<?> setPassword(@RequestBody NewPasswordDto newPassword) {
        return ResponseEntity.ok().build();
        // нужно создать один маппер
        // необходима проверка на ошибки: 401 и 403
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getInfoAboutAuthorizedUser() {
        return ResponseEntity.ok(new UserDto());
        // нужно создать один маппер
        // необходима проверка на ошибку 401
    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateUserDto> setInfoAboutAuthorizedUser(@RequestBody UpdateUserDto updateUser) {
        return ResponseEntity.ok(new UpdateUserDto());
        // нужно создать два маппера
        // необходима проверка на ошибку 401
    }

    @PatchMapping("/me/image")
    public ResponseEntity<?> setAvatar(@RequestBody String image) {
        return ResponseEntity.ok().build();
        // необходима проверка на ошибку 401
    }


}
