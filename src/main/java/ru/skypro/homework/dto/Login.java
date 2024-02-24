package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Login {
    @NotBlank
    @Size(min = 4, max = 32, message = "Логин должен быть в диапозоне от 4 до 32 символов!")
    private String username;
    @NotBlank
    @Size(min = 4, max = 16, message = "Пароль должен быть в диапозоне от 4 до 16 символов!")
    private String password;
}
