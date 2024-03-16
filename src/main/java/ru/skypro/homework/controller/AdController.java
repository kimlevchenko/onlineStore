package ru.skypro.homework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDtoIn;
import ru.skypro.homework.dto.AdExtendedDtoOut;
import ru.skypro.homework.dto.AdsDtoOut;
import ru.skypro.homework.dto.AdDtoOut;
import ru.skypro.homework.service.AdService;

import java.security.Principal;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("ads")
public class AdController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdController.class);
    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping
    //@PreAuthorize("permitAll()")
    public AdsDtoOut getAllAds() {
        LOGGER.info("Получен запрос для getAllAds");
        return adService.getAllAds();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)  //MULTIPART_FORM_DATA_VALUE, иначе сваггер предложит заполнить json
    public AdDtoOut createAd(@RequestPart("properties") AdDtoIn adDtoIn,
                             //если первый параметр - объект типа AdDtoIn,
                             //то Swagger не справится в такой посылкой, он пошлет строку.
                             //А в Postman надо, используя 3 точки, открыть колонку ТипКонтента и задать там application/json
                             @RequestPart MultipartFile image,
                             Principal principal) {
        LOGGER.info("Получен запрос для addAd: properties = " + adDtoIn + ", image = " + image);
        return  adService.createAd(adDtoIn, image, principal);
    }
    @GetMapping("{id}")
    public AdExtendedDtoOut getAdExtended(@PathVariable int id) {
        LOGGER.info("Получен запрос для getAdExtended: id = " + id);
        return adService.getAdById(id);
    }
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @CheckUserService.getUsernameByAd(#id) == principal.username")
    public ResponseEntity<String> deleteAd(@PathVariable int id) {
        LOGGER.info("Получен запрос для deleteAd: id = " + id);
        adService.deleteAd(id);  //если такой id не существует то возникнет EmptyResultDataAccessException
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasRole('ADMIN') or @CheckUserService.getUsernameByAd(#id) == principal.username")
    public AdDtoOut updateAd(@PathVariable int id, @RequestBody AdDtoIn adDtoIn) {
        LOGGER.info("Получен запрос для updateAd: id = " + id +", adDtoIn = " + adDtoIn);
        return adService.updateAd(id, adDtoIn);
    }
    @GetMapping("me")
    public AdsDtoOut getMyAds(Principal principal) {
        LOGGER.info("Получен запрос для getMyAds");
        return adService.getMyAds(principal);
    }
    @PatchMapping(value = "{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or @CheckUserService.getUsernameByAd(#id) == principal.username")
    public void updateImage(@PathVariable int id, @RequestPart MultipartFile image) {
        LOGGER.info("Получен запрос для updateImage:  id = " + id + ", image = " + image);
        adService.updateImage(id, image);
    }
    @GetMapping(value = "{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable int id) {
        LOGGER.info("Получен запрос для getImage:  id = " + id);
        byte[] photo = adService.getImage(id);
        //Фронт без заголовка ответа работает,
        //но сваггер перестает показывать фото и предлагает скачать файл неизвестного назначения
        //HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.IMAGE_JPEG);
        //headers.setContentLength(photo.length);
        //return new ResponseEntity<>(photo, headers, HttpStatus.OK);
        return new ResponseEntity<>(photo, HttpStatus.OK);
    }

}