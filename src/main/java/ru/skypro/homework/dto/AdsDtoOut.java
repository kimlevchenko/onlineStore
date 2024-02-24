package ru.skypro.homework.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdsDtoOut {
    private int count;
    private List<AdDtoOut> results;
}
