package ru.skypro.homework.entity;

import lombok.Data;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "`user`")  //таблица с именем user создается, только если имя указать в ``
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob
    @Column(columnDefinition = "oid")
    @Basic(fetch = FetchType.LAZY)  //Чтобы не читать зря - FetchType.LAZY
    private byte[] image;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Ad> ads;

    @Override  //если не создать toString то отладчик выдает OutOfMemory при показе user,
    //наверно пытается показать циклическую связь user-ad
    public String toString() {
        return "User(id=" + id + ", name='" + username + "', role=" + role + ")";
    }
}
