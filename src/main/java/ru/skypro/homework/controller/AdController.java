package ru.skypro.homework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdExtendedDtoOut;
import ru.skypro.homework.dto.AdsDtoOut;
import ru.skypro.homework.dto.AdDtoOut;

@RestController
@RequestMapping("ads")
public class AdController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdController.class);

    @GetMapping
    public AdsDtoOut getAllAds() {
        LOGGER.info("Получен запрос для getAllAds");
        return new AdsDtoOut();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AdDtoOut addAd(@RequestPart("properties") String jsonAdDtoIn,
                          @RequestPart MultipartFile image) {
        LOGGER.info("Получен запрос для addAd");
        return new AdDtoOut();
    }

    @GetMapping("{id}")
    public AdExtendedDtoOut getAdExtended(@PathVariable("id") int id) {
        LOGGER.info("Получен запрос для getAdExtended: id = " + id);
        return new AdExtendedDtoOut();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAd(@PathVariable("id") int id) {
        LOGGER.info("Получен запрос для DeleteAd: id = " + id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{id}")
    public AdDtoOut updateAd(@PathVariable("id") int id) {
        LOGGER.info("Получен запрос для updateAd: id = " + id);
        return new AdDtoOut();
    }

    @GetMapping("me")
    public AdsDtoOut getMyAds() {
        LOGGER.info("Получен запрос для getMyAds");
        return new AdsDtoOut();
    }

    @PatchMapping(value = "{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public byte[] updateImage(@PathVariable("id") int id, @RequestBody MultipartFile image) {
        LOGGER.info("Получен запрос для updateImage:  id = " + id + ", image = " + image);
        return new byte[0];
    }
}