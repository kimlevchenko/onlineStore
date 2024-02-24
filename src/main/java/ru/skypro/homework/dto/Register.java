package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class Register {

    private String username;
    private String password;
    private String login;
    private String phone;
    private Role role;
}
