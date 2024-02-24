package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class Register {
    @NotBlank
    @Size(min = 4, max = 32, message = "Логин должен быть в диапозоне от 4 до 32 символов!")
    @NotBlank
    private String username;
    @Size(min = 8, max = 16, message = "Пароль должен быть в диапозоне от 8 до 16 символов!")
    private String password;
    @NotBlank
    @Size(min = 2, max = 16, message = "Имя должно быть в диапозоне от 2 до 16 символов!")
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 16, message = "Фамилия должна быть в диапозоне от 2 до 16 символов!")
    private String lastName;
    @Pattern(regexp = "\\+7\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}",
            message = "Телефон должен соответствовать следующему шаблону: +7(000)000-00-00")
    private String phone;
    @NotNull(message = "Роль пользователя не должна быть пустой!")
    private Role role;
}
