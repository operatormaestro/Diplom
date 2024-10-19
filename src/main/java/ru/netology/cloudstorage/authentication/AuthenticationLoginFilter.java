package ru.netology.cloudstorage.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudstorage.dto.ExceptionResponse;
import ru.netology.cloudstorage.dto.LoginRequest;
import ru.netology.cloudstorage.dto.LoginResponse;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.repository.AuthenticationTokenRepository;
import ru.netology.cloudstorage.service.AuthenticationTokenService;

import java.io.IOException;

import static ru.netology.cloudstorage.configuration.StringConstants.AUTH_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationLoginFilter extends OncePerRequestFilter {
    private final AuthenticationTokenService authenticationTokenService;
    private final AuthenticationTokenRepository authTokenRepository;
    private final AuthenticationUserPasswordProvider authenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException {
        if (request.getHeader(AUTH_TOKEN) == null) {
            String bodyJSON = request.getReader().readLine();
            if (bodyJSON != null) {
                ObjectMapper mapper = new ObjectMapper();
                LoginRequest userDto = mapper.readValue(bodyJSON, LoginRequest.class);
                String username = userDto.getLogin();
                String password = userDto.getPassword();
                try {
                    Authentication authentication = new UsernamePasswordAuthentication(username, password, null);
                    authentication = authenticationProvider.authenticate(authentication);
                    String authToken = authenticationTokenService.generatedAuthToken(authentication);
                    authTokenRepository.putAuthToken(username, authToken);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(mapper.writeValueAsString(new LoginResponse(authToken)));
                    response.getWriter().flush();
                    log.info("User {} are authenticate. Current token: {}", username, authToken);
                } catch (BadCredentialsException | ObjectNotFoundException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(mapper.writeValueAsString(
                            new ExceptionResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage())));
                    response.getWriter().flush();
                }
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/login");
    }
}

