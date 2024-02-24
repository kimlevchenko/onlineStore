package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UpdateUserDto {
    @NotBlank
    @Size(min = 3, max = 10, message = "Имя должно быть в диапозоне от 3 до 10 символов!")
    private String firstName;
    @NotBlank
    @Size(min = 3, max = 10, message = "Фамилия должна быть в диапозоне от 3 до 10 символов!")
    private String lastName;
    @Pattern(regexp = "\\+7\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}",
            message = "Телефон должен соответствовать следующему шаблону: +7(000)000-00-00")
    private String phone;

}
