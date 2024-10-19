package ru.netology.cloudstorage.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.configuration.StringConstants;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.service.AuthenticationTokenService;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.cloudstorage.StringTestConstants.USERNAME;

class AuthenticationTokenRepositoryTest extends CloudStorageApplicationTests {

    @Autowired
    AuthenticationTokenRepository authenticationTokenRepository;
    @Autowired
    AuthenticationTokenService authenticationTokenService;

    @BeforeEach
    void init() {
        Authentication authentication = new UsernamePasswordAuthentication(USERNAME, null, null);
        var token = authenticationTokenService.generatedAuthToken(authentication);
        authenticationTokenRepository.putAuthToken(USERNAME, token);
    }

    @Test
    void testAuthTokenInMemory() {
        var authToken = authenticationTokenRepository.getAuthTokenByUsername(USERNAME);

        assertTrue(authToken.isPresent());

        var claims = authenticationTokenService.getClaims(authToken.get());
        var username = String.valueOf(claims.get(StringConstants.USERNAME));

        assertEquals(username, USERNAME);
    }

    @Test
    void removeAuthTokenByUsername() {
        authenticationTokenRepository.removeAuthTokenByUsername(USERNAME);
        var authToken = authenticationTokenRepository.getAuthTokenByUsername(USERNAME);

        assertFalse(authToken.isPresent());
    }

}