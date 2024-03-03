package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Ad;

@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {
    //@Transactional  //для работы с Large Object.
    //иначе ошибка - Large Objects may not be used in auto-commit mode - при getMyAds
    //List<Ad> findAllByUserId(Integer userId); //это было временно. Теперь используем коллекцию из user
}
