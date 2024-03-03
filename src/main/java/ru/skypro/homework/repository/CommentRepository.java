package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.AdComment;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<AdComment, Integer> {

    @Transactional
    List<AdComment> findAllByAdId(Integer adId);

}
