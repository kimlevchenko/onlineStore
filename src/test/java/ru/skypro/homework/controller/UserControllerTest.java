package ru.skypro.homework.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
@Import(WebSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;
    @SpyBean
    private UserService userService;

    @SpyBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setPhone("+7852-123-45-67");
        user.setRole(Role.USER);
        user.setImage(new byte[]{1, 2, 3});
    }
    @Test
    @WithMockUser(username = "user")
    public void setPasswordTest() throws Exception {
        NewPasswordDto newPasswordDto = new NewPasswordDto();
        newPasswordDto.setCurrentPassword("password");
        newPasswordDto.setNewPassword("newPassword");

        String username = user.getUsername();

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return username;
            }
        };
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));


        mockMvc.perform(
                        post("/users/set_password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newPasswordDto))
                                .principal(principal))
                .andExpect(status().isOk());


        verify(userService, times(1))
                .setPassword(eq(newPasswordDto), any(Principal.class));
    }
    @Test
    @WithMockUser(username = "user")
    public void getInfoAboutAuthorizedUserTest() throws Exception {
        UserDto expected = userMapper.userToUserDto(user);

        String username = user.getUsername();

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return username;
            }
        };
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(
                        get("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    UserDto userDto = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            UserDto.class
                    );
                    assertThat(userDto).isEqualTo(expected);
                });
        verify(userService, times(1)).getInfoAboutAuthorizedUser(any(Principal.class));
    }
    @Test
    @WithMockUser(username = "user")
    public void setInfoAboutAuthorizedUser() throws Exception {
        UpdateUserDto expected = new UpdateUserDto();
        expected.setFirstName("Petr");
        expected.setLastName("Petrov");
        expected.setPhone("+7985-111-11-11");

        String username = user.getUsername();

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return username;
            }
        };
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(
                        patch("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(expected))
                                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    UpdateUserDto updateUserDtoResult = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            UpdateUserDto.class
                    );
                    assertThat(updateUserDtoResult).isEqualTo(expected);
                });
        verify(userService, times(1)).setInfoAboutAuthorizedUser(eq(expected), any(Principal.class));
    }
    @Test
    @WithMockUser(username = "user")
    public void setAvatarTest() throws Exception {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "someImage".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/me/image")
                        .file(file)
                        .param("image", "test.jpg")
                        .with(request -> {
                            request.setMethod("PATCH");
                            request.setRemoteUser("user");
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        verify(userService, times(1))
                .setAvatar(eq(file), any(Principal.class));
    }
    @Test
    @WithMockUser(username = "user")
    public void getAvatarTest() throws Exception {
        String username = user.getUsername();

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return username;
            }
        };
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/me/image")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(result -> {
                    byte[] imageBytes = result.getResponse().getContentAsByteArray();
                    assertThat(imageBytes).isNotNull().isNotEmpty();
                    assertThat(imageBytes.length).isEqualTo(user.getImage().length);
                });

        verify(userService, times(1)).getAvatar(any(Principal.class));
    }
    @Test
    @WithMockUser(username = "user")
    public void getAvatarByIdTest() throws Exception {
        int userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/" + userId + "/image")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(result -> {
                    byte[] imageBytes = result.getResponse().getContentAsByteArray();
                    assertThat(imageBytes).isNotNull().isNotEmpty();
                    assertThat(imageBytes.length).isEqualTo(user.getImage().length);
                });

        verify(userService, times(1)).getAvatarById(userId);
    }

}
