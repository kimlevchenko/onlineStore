package ru.skypro.homework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

import java.security.Principal;


@RestController
@RequestMapping("users")
@CrossOrigin(value = "http://localhost:3000")
public class UserController {
    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("set_password")
    public ResponseEntity<?> setPassword(@RequestBody NewPasswordDto newPasswordDto,
                                         Principal principal) {
        LOGGER.info(String.format("Получен запрос для setPassword: newPasswordDto = %s, " +
                "user = %s", newPasswordDto, principal.getName()));
        userService.setPassword(newPasswordDto, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("me")
    public ResponseEntity<UserDto> getInfoAboutAuthorizedUser(Principal principal) {
        LOGGER.info(String.format("Получен запрос для getInfoAboutAuthorizedUser: user = %s", principal.getName()));
        return ResponseEntity.ok().body(userService.getInfoAboutAuthorizedUser(principal));
    }

    @PatchMapping("me")
    public ResponseEntity<UpdateUserDto> setInfoAboutAuthorizedUser(@RequestBody UpdateUserDto updateUserDto,
                                                                    Principal principal) {
        LOGGER.info(String.format("Получен запрос для setInfoAboutAuthorizedUser: updateUserDto = %s, " +
                "user = %s", updateUserDto, principal.getName()));
        return ResponseEntity.ok().body(userService.setInfoAboutAuthorizedUser(updateUserDto, principal));
    }

    @PatchMapping(path = "me/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> setAvatar(@RequestPart("image") MultipartFile image,
                                       Principal principal) {
        LOGGER.info(String.format("Получен запрос для setAvatar: image = %s, " +
                "user = %s", image, principal.getName()));
        userService.setAvatar(image, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "me/image")
    public ResponseEntity<byte[]> getAvatar(Principal principal) {
        LOGGER.info(String.format("Получен запрос для getAvatar: user = %s", principal.getName()));
        byte[] imageBytes = userService.getAvatar(principal);
        return new ResponseEntity<>(imageBytes, HttpStatus.OK);
    }

    @GetMapping(path = "{userId}/image")
    public ResponseEntity<byte[]> getAvatarById(@PathVariable Integer userId) {
        LOGGER.info(String.format("Получен запрос для getAvatar: userId = %s", userId));
        byte[] imageBytes = userService.getAvatarById(userId);
        return new ResponseEntity<>(imageBytes, HttpStatus.OK);
    }

}
