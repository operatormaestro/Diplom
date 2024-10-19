package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.configuration.StringConstants;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.repository.AuthenticationTokenRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.cloudstorage.StringTestConstants.USERNAME;

class AuthenticationTokenServiceTest extends CloudStorageApplicationTests {

    @Autowired
    AuthenticationTokenService authenticationTokenService;
    @Autowired
    AuthenticationTokenRepository authenticationTokenRepository;

    @Test
    void testAuthToken() {
        Authentication authentication = new UsernamePasswordAuthentication(USERNAME, null, null);
        var token = authenticationTokenService.generatedAuthToken(authentication);
        authenticationTokenRepository.putAuthToken(USERNAME, token);

        var claims = authenticationTokenService.getClaims(token);
        var username = String.valueOf(claims.get(StringConstants.USERNAME));
        var isValidToken = authenticationTokenService.isValidAuthToken(token);

        assertEquals(username, USERNAME);
        assertTrue(isValidToken);
    }

}