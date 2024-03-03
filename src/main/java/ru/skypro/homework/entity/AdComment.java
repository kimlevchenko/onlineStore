package ru.skypro.homework.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс AdComment комментарий объявления <br>
 * {@link Integer} id  - идентификатор комментария (not null)<br>
 * {@link String} text  - текст комментария (not null)<br>
 * {@link LocalDateTime} createdAt  - дата и время создания комментария (not null)<br>
 * {@link Ad} ad  - комментируемое объявление (not null)<br>
 * {@link User} user  - автор комментария (not null)<br>
 */
@Data
@Entity
@Table(name = "ad_comment")
public class AdComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    private LocalDateTime createdAt;
    @ManyToOne
    private Ad ad;
    @ManyToOne
    private User user;
}
