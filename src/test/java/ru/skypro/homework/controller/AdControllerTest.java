package ru.skypro.homework.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.dto.AdDtoOut;
import ru.skypro.homework.dto.AdExtendedDtoOut;
import ru.skypro.homework.dto.AdsDtoOut;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.dto.AdDtoIn;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.service.CheckUserService;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AdController.class)
@Import({WebSecurityConfig.class, CheckUserService.class})
public class AdControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AdRepository adRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CommentRepository commentRepository; //для конструктора CheckUserService
    @SpyBean
    private AdService adService;
    @SpyBean
    private AdMapper adMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationContext appContext;  //для отладки. Посмотреть какие бины есть

//почему-то созданный таким образом (через @SpyBean) бин не виден для
//@PreAuthorize("hasRole('ADMIN') or @CheckUserService.getUsernameByAd(#id) == principal.username")
//возникает NoSuchBeanDefinitionException
//Сделал через @Import
//@SpyBean
//private CheckUserService checkUserService;
@Test
//без этой аннотации MockMvc.perform будет выдавать NullPointerException
@WithMockUser(username = "user")
public void createAdTest()  throws Exception {

    //готовим тело запроса
    AdDtoIn adDtoIn = new AdDtoIn();
    adDtoIn.setTitle("MyAd");
    adDtoIn.setPrice(123);
    adDtoIn.setDescription("Описание");

    //готовим затычку для userRepository.findByUsername
    User user = new User();
    user.setId(1);
    user.setUsername("user");
    //затыкаем userRepository.findByUsername
    Principal principal = mock(Principal.class); //этого principal будем передавать в adMapper.toEntity
    when(principal.getName()).thenReturn("user"); //имя принципала будем искать в базе
    when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

    //готовим затычку для adRepository.save
    Ad adBeforeSave = adMapper.toEntity(adDtoIn, principal);
    Ad adAfterSave = adMapper.toEntity(adDtoIn, principal); //нужна другая ссылка, отличная от adBeforeSave
    adAfterSave.setId(123);
    //Если в save задать adBeforeSave, то сравнение не удастся и заглушка не сработает
    //Поэтому затыкаю через any(), но потом проверю параметр
    when(adRepository.save(any())).thenReturn(adAfterSave);

    AdDtoOut expectedAdDtoOut = adMapper.toAdDtoOut(adAfterSave);
//MockPart filePart = new MockPart("image", new byte[3]);
    //filePart.getHeaders().setContentType(MediaType.IMAGE_JPEG);
    //Cоздать два MockPart не удалось.
    //mockMvc.perform передает обе части (и properties, и image) в параметрах
    //а надо в теле. В результате тело (пустое) не идентифицируется контроллером.
    //Однако получилось передать в file и part
    MockMultipartFile file = new MockMultipartFile("image", "123".getBytes());
    MockPart adDtoInPart = new MockPart("properties", objectMapper.writeValueAsString(adDtoIn).getBytes());
    adDtoInPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(multipart(HttpMethod.POST,"/ads")
                    .file(file)
                    .part(adDtoInPart) //objectMapper.writeValueAsString(adDtoIn))
            ).andExpect(status().isOk())
            .andExpect(result -> {
                String content = result.getResponse().getContentAsString();
                AdDtoOut actualAdDtoOut = objectMapper.readValue(content, AdDtoOut.class);
                //проверяем, что к нам вернулся объект, которым мы замокали репозиторий
                assertThat(actualAdDtoOut).isEqualTo(expectedAdDtoOut);
            });

    //Проверим, чем мы накормили adRepository.save
    ArgumentCaptor<Ad> adCaptor = ArgumentCaptor.forClass(Ad.class);
    verify(adRepository,times(1)).save(adCaptor.capture());
    Ad adFact = adCaptor.getValue();
    //assertThat(adBeforeSave).isEqualTo(adFact); //так не равны
    //assertEquals(adBeforeSave, adFact);         //и так не равны, поэтому сравниваем поля
    assertEquals(adBeforeSave.getId(), adFact.getId()); //сравнение null дает истину
    assertEquals(adBeforeSave.getUser().getId(), adFact.getUser().getId());
    assertEquals(adBeforeSave.getUser().getUsername(), adFact.getUser().getUsername());
    assertEquals(adBeforeSave.getPrice(), adFact.getPrice());
    assertEquals(adBeforeSave.getTitle(), adFact.getTitle());
}
    @Test
    @WithMockUser(username = "user", roles = "USER")
    //USER специально, чтобы задействовать @CheckUserService
    public void deleteAdTest()  throws Exception {
        //готовим затычку для adRepository.findById - вызывается из CheckUserService
        Ad ad = new Ad();
        ad.setId(333);
        ad.setUser(new User());
        ad.getUser().setUsername("user"); //user - имя принципала по умолчанию

        when(adRepository.findById(123)).thenReturn(Optional.of(ad));
        mockMvc.perform(delete("/ads/123")
        ).andExpect(status().isOk());

        //Проверим, чем мы накормили adRepository.deleteById
        ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(adRepository,times(1)).deleteById(intCaptor.capture());
        Integer fact = intCaptor.getValue();
        assertEquals(123, fact);
    }
    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void updateAdTest()  throws Exception {
        //готовим тело запроса с параметром 111
        AdDtoIn adDtoIn = new AdDtoIn();
        adDtoIn.setTitle("MyAd");
        adDtoIn.setPrice(123);

        //готовим затычку для adRepository.findById
        Ad adFromRepository = new Ad();
        adFromRepository.setId(222);
        adFromRepository.setTitle("Старое название");
        when(adRepository.findById(111)).thenReturn(Optional.of(adFromRepository));

        //готовим затычку для adRepository.save
        Ad adAfterSave = new Ad();
        adAfterSave.setId(333); //все id специально разные, чтобы подтвердить передачу информации по этапам
        adAfterSave.setUser(new User()); //чтобы не было NullPointerException при формировании DtoOut
        adAfterSave.getUser().setId(321); //чтобы не было NullPointerException при формировании DtoOut
        adAfterSave.setTitle("New Title");
        //Если в save задать adBeforeSave, то сравнение не удастся и заглушка не сработает
        //Поэтому затыкаю через any(), но потом проверю параметр save
        when(adRepository.save(any())).thenReturn(adAfterSave);

        //этот ожидаемый результат гарантирован, если сработает заглушка save
        AdDtoOut expectedAdDtoOut = adMapper.toAdDtoOut(adAfterSave);
        mockMvc.perform(patch("/ads/111")
                        .content(objectMapper.writeValueAsString(adDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    AdDtoOut actualAdDtoOut = objectMapper.readValue(content, AdDtoOut.class);
                    //проверяем, что к нам вернулся объект, которым мы замокали репозиторий
                    assertThat(actualAdDtoOut).isEqualTo(expectedAdDtoOut);
                });

        //Проверим, чем мы накормили adRepository.save
        ArgumentCaptor<Ad> adCaptor = ArgumentCaptor.forClass(Ad.class);
        verify(adRepository,times(1)).save(adCaptor.capture());
        Ad adArgumentOfSave = adCaptor.getValue();
        //Id на входе в save д.б. равен прочитанному из репозитория
        assertEquals(adFromRepository.getId(), adArgumentOfSave.getId()); //222
        //Цена и заголовок д.б. равны переданным в запросе
        assertEquals(adDtoIn.getPrice(), adArgumentOfSave.getPrice());    //123
        assertEquals(adDtoIn.getTitle(), adArgumentOfSave.getTitle());    //MyAd
    }
    @Test
    @WithMockUser(username = "user") //иначе 401 Unauthorized, ведь Spring должен подложить principal в параметры
    public void getMyAdsTest()  throws Exception {
        //готовим затычку для userRepository.findByUsername
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        //созданный юзер содержит коллекцию объявлений, которые должны вернуться
        Ad ad = new Ad();
        ad.setId(222);
        ad.setUser(user);
        ad.setTitle("MyAd");
        user.setAds(Collections.singletonList(ad));
        //затыкаем userRepository.findByUsername
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/ads/me"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    AdsDtoOut actualAdsDtoOut = objectMapper.readValue(content, AdsDtoOut.class);
                    //проверяем, что к нам вернулся список, который мы пришили к пользователю
                    assertThat(actualAdsDtoOut).isEqualTo(adMapper.toAdsDtoOut(user.getAds()));
                });
    }@Test
    public void getAllAdsTest() throws Exception {
        //готовим затычку для adRepository.findAll
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        Ad ad = new Ad();
        ad.setId(222);
        ad.setUser(user);
        ad.setTitle("MyAd");
        List<Ad> listAd = Collections.singletonList(ad);
        //затыкаем adRepository.findAll
        when(adRepository.findAll()).thenReturn(listAd);
        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    AdsDtoOut actualAdsDtoOut = objectMapper.readValue(content, AdsDtoOut.class);
                    //проверяем, что к нам вернулся список, которым мы замокали репозиторий
                    assertThat(actualAdsDtoOut).isEqualTo(adMapper.toAdsDtoOut(listAd));
                });
    }@Test
    //Не разобрался, почему для запуска запроса потребовался WithMockUser. Кто подскажет?
    //У этого эндпойнта нет ни @PreAuthorize, ни principal.
    //Например, getAllAdsTest проходит без проблем без указания @WithMockUser
    @WithMockUser(username = "user") //если не задать - 401 Unauthorized
    public void getAdExtendedTest() throws Exception {
        //готовим затычку для adRepository.findById
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        Ad ad = new Ad();
        ad.setId(222);
        ad.setUser(user);
        ad.setTitle("MyAd");
        //затыкаем adRepository.findById.
        //К userRepository явно мы не обращаемся, поэтому его мокать не надо
        when(adRepository.findById(111)).thenReturn(Optional.of(ad));
        mockMvc.perform(get("/ads/111"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    AdExtendedDtoOut actualAdExtendedDtoOut = objectMapper.readValue(content, AdExtendedDtoOut.class);
                    //проверяем, что к нам вернулось объявление, которым мы замокали репозиторий
                    assertThat(actualAdExtendedDtoOut).isEqualTo(adMapper.toAdExtendedDtoOut(ad));
                });
    }
}
