package ru.job4j.cinema.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class AuthorizationFilter extends HttpFilter {
    private static final Set<String> URLS_PROTECT = Set.of(
            "/tickets"
    );

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String uri = request.getRequestURI();
        boolean userLoggedIn = request.getSession().getAttribute("user") != null;
        if (isUrlProtect(uri) && !userLoggedIn) {
            String loginPageUrl = request.getContextPath() + "/users/login";
            response.sendRedirect(loginPageUrl);
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isUrlProtect(String uri) {
        return URLS_PROTECT.stream().anyMatch(uri::startsWith);
    }
}
