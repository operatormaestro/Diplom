package ru.netology.cloudstorage.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.entity.Storage;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.cloudstorage.StringTestConstants.*;

class StorageRepositoryTest extends CloudStorageApplicationTests {

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void init() {

        assertTrue(userRepository.findByUsername(USERNAME).isPresent());

        var user = userRepository.findByUsername(USERNAME).get();
        storageRepository.save(
                new Storage(FILENAME_ONE, FILE_SIZE, FILENAME_ONE.getBytes(), user));
    }

    @AfterEach
    void clean() {
        storageRepository.deleteAll();
    }

    @Test
    void findAllByUser() {

        assertTrue(userRepository.findByUsername(USERNAME).isPresent());

        var user = userRepository.findByUsername(USERNAME).get();
        var storage = storageRepository.findAllByUser(user, Limit.of(3));

        assertFalse(storage.isEmpty());
        assertEquals(storage.size(), 1);
    }

    @Test
    void findByUserAndFileName() {

        assertTrue(userRepository.findByUsername(USERNAME).isPresent());

        var user = userRepository.findByUsername(USERNAME).get();
        var storage = storageRepository.findByUserAndFileName(user, FILENAME_ONE);

        assertTrue(storage.isPresent());
        assertEquals(storage.get().getFileName(), FILENAME_ONE);
        assertEquals(storage.get().getFileSize(), FILE_SIZE);
        assertEquals(storage.get().getUser().getUsername(), user.getUsername());
    }

    @Test
    void deleteByUserAndFileName() {
        assertTrue(userRepository.findByUsername(USERNAME).isPresent());

        var user = userRepository.findByUsername(USERNAME).get();
        storageRepository.deleteByUserAndFileName(user, FILENAME_ONE);
        var storage = storageRepository.findByUserAndFileName(user, FILENAME_ONE);

        assertFalse(storage.isPresent());
    }
}