package ru.skypro.homework.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDtoIn;
import ru.skypro.homework.dto.AdDtoOut;
import ru.skypro.homework.dto.AdExtendedDtoOut;
import ru.skypro.homework.dto.AdsDtoOut;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;


import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.function.Supplier;
@Service
public class AdService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserRepository userRepository;

    //Читать текст ошибки в теле ответа фронт не будет. Прокинем сообщение хотя бы для Logger
    private Supplier<EntityNotFoundException> excSuppl(int id) {
        return () -> new EntityNotFoundException("Ad with id " + id + " not found");
    }


    public AdService(AdRepository adRepository, AdMapper adMapper, UserRepository userRepository) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
        this.userRepository = userRepository;
    }

    /**
     * Метод для смены картинки объявления.<br>
     * Используется метод репозитория {@link JpaRepository#save(Object)}
     * @param id идентификатор объявленя.
     * @param image новая картинка объявления от контроллера.
     * @throws EntityNotFoundException если указанное в параметре объявление отсутствует.
     * @throws RuntimeException если IOException получено при чтении картинки.
     */
    public void updateImage(int id, MultipartFile image) {
        Ad ad = adRepository.findById(id).orElseThrow(excSuppl(id));
        try {
            ad.setImage(image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        adRepository.save(ad);
    }
/**
 * Извлекает из базы объявления пользователя.<br>
 * Используется метод репозитория userRepository.findByUserName
 * @param principal принципал, чье имя используется для отбора
 * @return коллекцию объявлений в формате выдачи, обернутую в {@link AdsDtoOut}
 * @throws UserNotFoundException если пользователь не найден в БД.
 */
@Transactional  //для получения user.getAds()
//без @Transactional возникает
//PSQLException: Большие объекты не могут использоваться в режиме авто-подтверждения (auto-commit).
//хотя мы не обращаемся ни к каким картинкам
public AdsDtoOut getMyAds(Principal principal) {
    String username = principal.getName();
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));
    List<Ad> ads = user.getAds();
    //List<Ad> ads = adRepository.findAllByUserId(user.getId()); было временное решение
    return adMapper.toAdsDtoOut(ads);
}
/**
 * Извлекает из базы все объявления, независимо от пользователя.<br>
 * Используется метод репозитория {@link JpaRepository#findAll}
 * @return коллекцию объявлений в формате выдачи, обернутую в {@link AdsDtoOut}
 */
public AdsDtoOut getAllAds() {
    return adMapper.toAdsDtoOut(adRepository.findAll());
}
/**
 * Извлекает из базы картинку объявления.<br>
 * Используется метод репозитория {@link JpaRepository#findById}
 * @param id идентификатор объявления
 * @return массив байтов картинки объявления
 * @throws EntityNotFoundException если указанное в параметре объявление отсутствует.
 */
@Transactional
public byte[] getImage(int id) {
    return adRepository.findById(id).orElseThrow(excSuppl(id)).getImage();
}
/**
 * Извлекает из базы объявление в расширенном формате.<br>
 * Используется метод репозитория {@link JpaRepository#findById}
 * @param id идентификатор объявления
 * @return объявление в расширенном формате {@link AdExtendedDtoOut}, с информацией об авторе
 * @throws EntityNotFoundException если указанное в параметре объявление отсутствует.
 */
public AdExtendedDtoOut getAdById(int id) {
    return adMapper.toAdExtendedDtoOut(adRepository.findById(id).orElseThrow(excSuppl(id)));
}
/**
 * Записывает в базу новое объявление.<br>
 * Используется метод репозитория {@link JpaRepository#save} для объявления
 * Используется метод репозитория userRepository.findByUserName для идентификации автора
 * @param adDtoIn информация о новом объявлении в формате входа {@link AdDtoIn}
 * @param image часть запроса, содержащая картинку
 * @param principal принципал, чье имя используется для идентификации автора
 * @return сохраненное объявление в формате вывода {@link AdDtoOut}
 * @throws UserNotFoundException если пользователь(автор) не найден в БД.
 * @throws RuntimeException если IOException получено при чтении картинки.
 */
public AdDtoOut createAd(AdDtoIn adDtoIn, MultipartFile image, Principal principal) {
    Ad adBeforeSave = adMapper.toEntity(adDtoIn, principal);
    try {
        adBeforeSave.setImage(image.getBytes());
    } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
    }
    //adAfterSave отличается от adBeforeSave тем, что в нем проставлен id
    Ad adAfterSave = adRepository.save(adBeforeSave);
    return adMapper.toAdDtoOut(adAfterSave);
}
/**
 * Обновляет в базе информацию об объявлении.<br>
 * Используется метод репозитория {@link JpaRepository#save}
 * @param id идентификатор обновлямого объявления
 * @param adDtoIn новая информация о объявлении в формате входа {@link AdDtoIn}
 * @return сохраненное объявление в формате вывода {@link AdDtoOut}
 * @throws EntityNotFoundException если указанное в параметре объявление отсутствует.
 */
public AdDtoOut updateAd(int id, AdDtoIn adDtoIn) {
    //Поскольку в AdDtoIn - не все реквизиты, а сохранить надо все,
    //придется читать Ad для получения недостающих.
    //Хотя запросом к базе можно обновить только требуемые.
    Ad ad = adRepository.findById(id).orElseThrow(excSuppl(id));
    ad.setTitle(adDtoIn.getTitle());
    ad.setPrice(adDtoIn.getPrice());
    ad.setDescription(adDtoIn.getDescription());
    return adMapper.toAdDtoOut(adRepository.save(ad));
}
/**
 * Удаляет в базе объявление вместе с комментариями к нему.<br>
 * Используется метод репозитория {@link JpaRepository#deleteById}
 * @param id идентификатор удаляемоого объявления
 * @throws org.springframework.dao.EmptyResultDataAccessException если указанное в параметре объявление отсутствует.
 */
public void deleteAd(int id) {
    adRepository.deleteById(id);
}
}
