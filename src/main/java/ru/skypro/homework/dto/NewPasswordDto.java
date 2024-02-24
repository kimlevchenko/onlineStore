package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewPasswordDto {
    @NotBlank
    @Size(min = 8, max = 16, message = "Текущий пароль должен быть в диапозоне от 8 до 16 символов!")
    private String currentPassword;
    @NotBlank
    @Size(min = 8, max = 16, message = "Новый пароль должен быть в диапозоне от 8 до 16 символов!")
    private String newPassword;

}
