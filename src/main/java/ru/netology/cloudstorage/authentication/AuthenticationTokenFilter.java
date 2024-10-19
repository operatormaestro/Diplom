package ru.netology.cloudstorage.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.service.AuthenticationTokenService;

import java.io.IOException;
import java.util.Optional;

import static ru.netology.cloudstorage.configuration.StringConstants.AUTH_TOKEN;
import static ru.netology.cloudstorage.configuration.StringConstants.USERNAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final AuthenticationTokenService authenticationTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationKey = request.getHeader(AUTH_TOKEN);
        log.info("Current Token: " + authorizationKey);
        if (Optional.ofNullable(authorizationKey).isPresent() && authorizationKey.startsWith("Bearer")) {
            authorizationKey = authorizationKey.replace("Bearer", "").trim();
            try {
                if (authenticationTokenService.isValidAuthToken(authorizationKey)) {
                    Claims claims = authenticationTokenService.getClaims(authorizationKey);
                    String username = String.valueOf(claims.get(USERNAME));
                    Authentication authentication = new UsernamePasswordAuthentication(username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException e) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/login");
    }
}

