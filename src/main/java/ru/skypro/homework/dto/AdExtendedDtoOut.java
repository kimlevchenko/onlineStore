package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class AdExtendedDtoOut {
    private int pk;
    private String authorFirstName;
    private String authorLastName;
    private String email;
    private String phone;
    private int price;
    private String title;
    private String description;
    private String image;
}
