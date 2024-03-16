package ru.skypro.homework.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.AdComment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

/**
 *  Сервис "CommentService".
 *  Cоздает, редактирует и удаляет комментарии к объявлениям т.е. записи в БД в таблице "ad_comment".<br>
 *  Находит список всех комментариев к данному объявлению.
 */
@Service
public class CommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          CommentMapper commentMapper,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userRepository = userRepository;
    }

    /**
     *  Cоздает запись в БД в таблице "ad_comment".<br>
     *  Используется метод репозитория {@link JpaRepository#save(Object)}
     *  @param adId идентификатор объявления
     *  @param createOrUpdateComment DTO,содержащее текст комментария
     *  @param userName имя автора комментария
     *  @return возвращает полную информацию о коментарии (текст, когда был создан, кем и к какому объявлению)
     * */
    @Transactional
    public Comment createComment(Integer adId,
                                 CreateOrUpdateComment createOrUpdateComment,
                                 String userName){
        LOGGER.info("Create comment.");
        return commentMapper.toDto(
                commentRepository.save(
                        commentMapper.toEntity(createOrUpdateComment, adId, getCurrentUser(userName).getId())));
    }
    /**
     *  Находит список всех комментариев к данному объявлению в БД в таблице "ad_comment".<br>
     *  Используется метод репозитория {@link JpaRepository#findAll()}
     *  @param adId идентификатор объявления
     *  @return возвращает список всех комментариев к данному объявлению
     * */
    @Transactional
    public Comments findComments(Integer adId){
        LOGGER.info("Find all comments.");
        return  commentMapper.toComments(commentRepository.findAllByAdId(adId));
    }

    /**
     *  Изменяет запись в БД в таблице "ad_comment".<br>
     *  Используется метод репозитория {@link JpaRepository#save(Object)}
     *  @param adId идентификатор объявления
     *  @param commentId идентификатор редактируемого комментария
     *  @param createOrUpdateComment DTO,содержащее текст комментария
     *  @param userName имя автора комментария
     *  @throws EntityNotFoundException если объявления или комментария с указанными идентификаторами нет в БД.
     *  @return возвращает полную информацию о коментарии (текст, когда был создан, кем и к какому объявлению)
     * */
    @Transactional
    public Comment updateComment(Integer adId,
                                 Integer commentId,
                                 CreateOrUpdateComment createOrUpdateComment,
                                 String userName){
        LOGGER.info("Update comment.");
        AdComment oldComment = commentRepository.findById(commentId).
                orElseThrow(() -> new EntityNotFoundException("Comment not found."));
        if(!oldComment.getAd().getId().equals(adId)){
            throw new EntityNotFoundException("You can't change the ad in a comment");
        }
        oldComment.setText(createOrUpdateComment.getText());
        oldComment.setCreatedAt(LocalDateTime.now());
        oldComment.setUser(getCurrentUser(userName));
        return commentMapper.toDto(commentRepository.save(oldComment));
    }

    /**
     *  Удаляет запись из БД в таблице "ad_comment".<br>
     *  Используется метод репозитория {@link JpaRepository#delete(Object)}
     *  @param adId идентификатор объявления
     *  @param commentId идентификатор удаляемого комментария
     *  @throws EntityNotFoundException если объявления или комментария с указанными идентификаторами нет в БД.
     * */
    @Transactional
    public void deleteComment(Integer adId, Integer commentId){
        LOGGER.info("Delete comment.");
        AdComment deletedComment = commentRepository.findById(commentId).
                orElseThrow(() -> new EntityNotFoundException("Comment not found."));
        if(!deletedComment.getAd().getId().equals(adId)){
            throw new EntityNotFoundException("Comment not found.");
        }
        commentRepository.delete(deletedComment);
    }
    private User getCurrentUser(String userName){
        return userRepository.findByUsername(userName).
                orElseThrow(() -> new UserNotFoundException("User not found."));
    }
}
