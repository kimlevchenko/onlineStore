package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class AdDtoIn {
    private String title;       //4-32
    private int price;        //0-10000000
    private String description; //8-64
}
