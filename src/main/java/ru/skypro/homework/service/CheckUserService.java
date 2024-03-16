package ru.skypro.homework.service;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdExtendedDtoOut;
import ru.skypro.homework.repository.CommentRepository;

import javax.persistence.EntityNotFoundException;

@Service("CheckUserService")
public class CheckUserService {

    private final AdService adService;
    private final CommentRepository commentRepository;

    public CheckUserService(AdService adService, CommentRepository commentRepository) {
        this.adService = adService;
        this.commentRepository = commentRepository;
    }

    /**
     * Метод ищет username пользователя для конкретного объявления. Метод используется
     * для проверки авторизации в аннотациях @PreAuthorize класса AdController.
     * @param id идентификатор объявления.
     * @return username пользователя.
     */
    public String getUsernameByAd(int id) {
        AdExtendedDtoOut adExtendedDtoOut = adService.getAdById(id);
        return adExtendedDtoOut.getEmail();
    }

    /**
     * Метод ищет username пользователя для конкретного комментария. Метод используется
     * для проверки авторизации в аннотациях @PreAuthorize класса CommentController.
     * @param id идентификатор комментария.
     * @return username пользователя.
     * @throws EntityNotFoundException выбрасывается, если комментарий не найден.
     */
    public String getUsernameByComment(int id) {
        return commentRepository.findById(id).
                orElseThrow(()-> new EntityNotFoundException("Comment not found")).
                getUser().getUsername();
    }

}
