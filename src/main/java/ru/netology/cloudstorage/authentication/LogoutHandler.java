package ru.netology.cloudstorage.authentication;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import ru.netology.cloudstorage.repository.AuthenticationTokenRepository;
import ru.netology.cloudstorage.service.AuthenticationTokenService;

import java.io.IOException;
import java.util.Optional;

import static ru.netology.cloudstorage.configuration.StringConstants.AUTH_TOKEN;
import static ru.netology.cloudstorage.configuration.StringConstants.USERNAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class LogoutHandler extends HttpStatusReturningLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    AuthenticationTokenService authenticationTokenService;
    @Autowired
    AuthenticationTokenRepository authTokenRepository;


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String authorizationKey = request.getHeader(AUTH_TOKEN);
        if (Optional.ofNullable(authorizationKey).isPresent() && authorizationKey.startsWith("Bearer")) {
            authorizationKey = authorizationKey.replace("Bearer", "").trim();
            Claims claims = authenticationTokenService.getClaims(authorizationKey);
            String username = String.valueOf(claims.get(USERNAME));

            log.info("Logout {} user. Current token: {}.", username, authorizationKey);

            authTokenRepository.removeAuthTokenByUsername(username);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        super.onLogoutSuccess(request, response, authentication);
    }
}
