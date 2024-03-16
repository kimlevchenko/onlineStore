package ru.skypro.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import ru.skypro.homework.dto.Role;

import static org.springframework.security.config.Customizer.withDefaults;
//конфигурация для веб-безопасности в приложении.
// В этом коде настраиваются правила доступа к различным URL-адресам и методам,
// а также используется шифрование паролей.
@Configuration //Аннотация @Configuration указывает, что данный класс является конфигурационным классом Spring.
@EnableGlobalMethodSecurity(prePostEnabled = true) //Аннотация @EnableGlobalMethodSecurity(prePostEnabled = true)
// включает использование аннотаций Spring Security для аннотирования методов с разрешениями доступа.
public class WebSecurityConfig {
// массив строк auth_whitelist, который содержит список URL-адресов.
    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars/**",
            "/login",
            "/register",
            "/ads",
            "/users/me/image",
            "/users/*/image",
            "/ads/*/image"
    };

   // filterChain(HttpSecurity http) настраивает правила доступа к URL-адресам и методам при помощи объекта HttpSecurity.
    @Bean//-аннотация, которая говорит Spring о том, что возвращаемое значение метода должно быть
    // зарегистрировано как бин в контейнере Spring.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf() //это действие отключает CSRF (Cross-Site Request Forgery) защиту.
                .disable()
                .authorizeHttpRequests( //authorizeRequests(authorization -> authorization - это начало конфигурации
                        // разрешений для доступа к различным URL-адресам.
                        authorization ->
                                authorization
                                        .mvcMatchers(AUTH_WHITELIST)
                                        .permitAll()//.mvcMatchers(auth_whitelist).permitAll() - это разрешает доступ
                                        // к URL-адресам, указанным в auth_whitelist, без аутентификации.
                                        .mvcMatchers("/ads/**", "/users/**")
                                        .authenticated())//.mvcMatchers("/ads/**", "/users/**").authenticated() -
                // это требует аутентификацию для доступа к URL-адресам, начинающимся с "/ads/" и "/users/".
                .cors()
                .and()//.cors().and() - это включает поддержку Cross-Origin Resource Sharing (CORS).
                .httpBasic(withDefaults());// .httpBasic().withDefaults() - это настраивает аутентификацию по схеме
        // HTTP Basic с использованием настроек по умолчанию.
        return http.build();//- это возвращает настроенный объект http.
    }
//passwordEncoder() возвращает объект типа PasswordEncoder для шифрования паролей.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
