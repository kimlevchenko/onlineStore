package ru.skypro.homework.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserServiceConfig;
import ru.skypro.homework.service.impl.AuthServiceImpl;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserServiceConfig userServiceConfig;

    @MockBean
    private AuthServiceImpl authService;

    @SpyBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void loginTest() throws Exception {
        Login login = new Login();
        login.setUsername("username");
        login.setPassword("password");
        when(authService.login(login.getUsername(), login.getPassword())).thenReturn(false);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }@Test
    public void loginReturnsUnauthorizedTest() throws Exception {
        Login login = new Login();
        login.setUsername("username");
        login.setPassword("password");
        when(authService.login(login.getUsername(), login.getPassword())).thenReturn(false);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void registerTest() throws Exception {
        Register register = new Register();
        register.setUsername("username");
        register.setPassword("password");
        register.setFirstName("Ivan");
        register.setLastName("Ivanov");
        register.setPhone("+7852-123-45-67");
        register.setRole(Role.USER);
        when(authService.register(register)).thenReturn(true);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());
    }
    @Test
    public void registerReturnsBadRequestTest() throws Exception {
        Register register = new Register();
        register.setUsername("username");
        register.setPassword("password");
        register.setFirstName("Ivan");
        register.setLastName("Ivanov");
        register.setPhone("+7852-123-45-67");
        register.setRole(Role.USER);
        when(authService.register(register)).thenReturn(false);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(register)))
                .andExpect(status().isBadRequest());
    }

}
