package ru.skypro.homework.filter;


import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//фильтр, который добавляет заголовок "access-control-allow-credentials" со значением "true" в ответ
// на каждый запрос. Затем фильтр передает запрос и ответ дальше в цепочку фильтров для дальнейшей обработки.
@Component
public class BasicAuthCorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");//Доступ-Контроль-Разрешить-Учетные данные
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
