package ru.skypro.homework.dto;

import lombok.Data;

@Data
public class AdDtoOut {
    private int pk;     //id объявления
    private int author; //id автора объявления
    private int price;  //цена объявления
    private String title;   //заголовок объявления
    private String image;   //ссылка на картинку объявления
}
