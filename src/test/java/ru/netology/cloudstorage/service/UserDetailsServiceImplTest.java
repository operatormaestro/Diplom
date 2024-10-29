package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.cloudstorage.StringTestConstants.USERNAME;
import static ru.netology.cloudstorage.StringTestConstants.USERNAME_WRONG;

class UserDetailsServiceImplTest extends CloudStorageApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername() {
        var user = userRepository.findByUsername(USERNAME);
        var userDetail = userDetailsService.loadUserByUsername(USERNAME);


        assertTrue(user.isPresent());
        assertEquals(userDetail.getUsername(), user.get().getUsername());
        assertEquals(userDetail.getPassword(), user.get().getPassword());
    }

    @Test
    void loadUserByUsernameException() {
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(USERNAME_WRONG));
    }
}