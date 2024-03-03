package ru.skypro.homework.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdDtoIn;
import ru.skypro.homework.dto.AdDtoOut;
import ru.skypro.homework.dto.AdExtendedDtoOut;
import ru.skypro.homework.dto.AdsDtoOut;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.repository.UserRepository;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdMapper {

    private final UserRepository userRepository;

    /**
     * Преобразует информацию об объявлении во входном формате в объект класса Ad.<br>
     * Используется метод репозитория userRepository.findByUserName для идентификации автора
     *
     * @param adDtoIn   информация об объявлении во входном формате {@link AdDtoIn}.
     * @param principal принципал, чье имя используется для идентификации автора
     * @return объект класса {@link Ad}.
     */
    public Ad toEntity(AdDtoIn adDtoIn, Principal principal) {
        Ad ad = new Ad();
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        ad.setUser(user);
        ad.setTitle(adDtoIn.getTitle());
        ad.setPrice(adDtoIn.getPrice());
        ad.setDescription(adDtoIn.getDescription());
        return ad;
    }

    /**
     * Преобразует объект класса Ad в выходной формат.<br>
     *
     * @param ad объект класса {@link Ad}
     * @return информация об объявлении во выходном формате {@link AdDtoOut}.
     */
    public AdDtoOut toAdDtoOut(Ad ad) {
        if (ad == null) return null;
        AdDtoOut adDtoOut = new AdDtoOut();
        adDtoOut.setPk(ad.getId());
        adDtoOut.setAuthor(ad.getUser().getId());
        adDtoOut.setTitle(ad.getTitle());
        adDtoOut.setPrice(ad.getPrice());
        //http://localhost:8080/ads/5/image
        adDtoOut.setImage("/ads/" + ad.getId() + "/image"); //http://localhost:8080/ - не указываем

        return adDtoOut;
    }

    /**
     * Преобразует объект класса Ad в расширенный выходной формат с подробной информацией об авторе.<br>
     *
     * @param ad объект класса {@link Ad}
     * @return информация об объявлении в расширенном выходном формате {@link AdExtendedDtoOut}.
     */
    public AdExtendedDtoOut toAdExtendedDtoOut(Ad ad) {
        AdExtendedDtoOut adExtendedDtoOut = new AdExtendedDtoOut();
        adExtendedDtoOut.setPk(ad.getId());
        adExtendedDtoOut.setAuthorFirstName(ad.getUser().getFirstName());
        adExtendedDtoOut.setAuthorLastName(ad.getUser().getLastName());
        adExtendedDtoOut.setEmail(ad.getUser().getUsername());
        adExtendedDtoOut.setPhone(ad.getUser().getPhone());
        adExtendedDtoOut.setTitle(ad.getTitle());
        adExtendedDtoOut.setPrice(ad.getPrice());
        adExtendedDtoOut.setDescription(ad.getDescription());
        adExtendedDtoOut.setImage("/ads/" + ad.getId() + "/image");
        return adExtendedDtoOut;
    }

    /**
     * Преобразует список объявлений в выходной формат с указанием количества элементов списка
     *
     * @param list список объектов класса {@link Ad}
     * @return список объявлений в выходном формате {@link AdsDtoOut} с элементами класса {@link AdDtoOut}.
     */
    public AdsDtoOut toAdsDtoOut(List<Ad> list) {
        if (list == null) {
            list = Collections.EMPTY_LIST;
        } //для тестов и на всякий случай
        AdsDtoOut adsDtoOut = new AdsDtoOut();
        adsDtoOut.setCount(list.size());
        adsDtoOut.setResults(list.stream().map(this::toAdDtoOut).collect(Collectors.toList()));
        return adsDtoOut;
    }
}