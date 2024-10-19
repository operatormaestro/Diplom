package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.dto.FileNameEditRequest;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.entity.Storage;
import ru.netology.cloudstorage.exceptions.BadRequestException;
import ru.netology.cloudstorage.exceptions.InternalServerException;
import ru.netology.cloudstorage.repository.StorageRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.cloudstorage.StringTestConstants.*;

class StorageServiceTest extends CloudStorageApplicationTests {

    @Autowired
    StorageService storageService;
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

        Authentication authentication = new UsernamePasswordAuthentication(USERNAME, null, null);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void clean() {
        storageRepository.deleteAll();
    }

    @Test
    void uploadFile() {
        byte[] bytes = FILENAME_TWO.getBytes();
        MockMultipartFile mockMultipartFile = new MockMultipartFile(FILENAME_TWO, bytes);
        storageService.uploadFile(FILENAME_TWO, mockMultipartFile);

        assertTrue(userRepository.findByUsername(USERNAME).isPresent());

        var user = userRepository.findByUsername(USERNAME).get();
        var storage = storageRepository.findByUserAndFileName(user, FILENAME_TWO);

        assertTrue(storage.isPresent());
        assertEquals(storage.get().getFileName(), FILENAME_TWO);
        assertEquals(storage.get().getUser().getUsername(), user.getUsername());
    }

    @Test
    void uploadFileError() {
        assertThrows(InternalServerException.class, () -> storageService.uploadFile(FILENAME_ONE, null));
    }

    @Test
    void getFileList() {
        var fileList = storageService.getFileList(3);

        assertFalse(fileList.isEmpty());
        assertEquals(fileList.size(), 1);
        assertEquals(fileList.get(0).getFilename(), FILENAME_ONE);
        assertEquals(fileList.get(0).getSize(), FILE_SIZE);
    }

    @Test
    void downloadFile() {
        byte[] bytesActual = FILENAME_ONE.getBytes();

        var bytesExpected = storageService.downloadFile(FILENAME_ONE);

        assertTrue(bytesExpected.length > 0);
        assertArrayEquals(bytesExpected, bytesActual);
    }

    @Test
    void downloadFileError() {
        assertThrows(BadRequestException.class, () -> storageService.downloadFile(FILENAME_WRONG));
    }

    @Test
    void deleteFile() {
        storageService.deleteFile(FILENAME_ONE);

        assertTrue(userRepository.findByUsername(USERNAME).isPresent());

        var user = userRepository.findByUsername(USERNAME).get();
        var storage = storageRepository.findByUserAndFileName(user, FILENAME_ONE);

        assertFalse(storage.isPresent());
    }

    @Test
    void deleteFileError() {
        assertThrows(BadRequestException.class, () -> storageService.deleteFile(FILENAME_WRONG));
    }

    @Test
    void editFileName() {
        storageService.editFileName(FILENAME_ONE, new FileNameEditRequest(FILENAME_TWO));

        assertTrue(userRepository.findByUsername(USERNAME).isPresent());

        var user = userRepository.findByUsername(USERNAME).get();
        var storage = storageRepository.findByUserAndFileName(user, FILENAME_TWO);

        assertTrue(storage.isPresent());
        assertEquals(storage.get().getFileName(), FILENAME_TWO);
        assertEquals(storage.get().getUser().getUsername(), user.getUsername());
    }

    @Test
    void editFileNameError() {
        assertThrows(BadRequestException.class, () -> storageService.editFileName(FILENAME_WRONG,
                new FileNameEditRequest(FILENAME_TWO)));
    }

}