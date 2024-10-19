package ru.netology.cloudstorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.cloudstorage.CloudStorageApplicationTests;
import ru.netology.cloudstorage.dto.FileListResponse;
import ru.netology.cloudstorage.dto.FileNameEditRequest;
import ru.netology.cloudstorage.dto.UsernamePasswordAuthentication;
import ru.netology.cloudstorage.service.StorageService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.netology.cloudstorage.StringTestConstants.*;
import static ru.netology.cloudstorage.configuration.StringConstants.FILENAME_PARAMETER;
import static ru.netology.cloudstorage.configuration.StringConstants.LIMIT_PARAMETER;

class StorageControllerTest extends CloudStorageApplicationTests {

    private static final String FILE_ENDPOINT = "/file";
    private static final String LIST_FILE_ENDPOINT = "/list";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    StorageService storageService;

    @Test
    void getAllFiles() throws Exception {
        setAuthentication();
        List<FileListResponse> list = List.of(FileListResponse.builder()
                .filename(FILENAME_ONE)
                .size(FILE_SIZE)
                .build());

        when(storageService.getFileList(any())).thenReturn(list);

        var result = mockMvc.perform(get(LIST_FILE_ENDPOINT).param(LIMIT_PARAMETER, String.valueOf(1)))
                .andExpect(status().isOk())
                .andReturn();
        var resultArray = objectMapper.readValue(result.getResponse().getContentAsString(), FileListResponse[].class);

        assertEquals(resultArray.length, 1);
        assertEquals(resultArray[0].getFilename(), list.get(0).getFilename());
        assertEquals(resultArray[0].getSize(), list.get(0).getSize());
    }

    @Test
    void getAllFilesException() throws Exception {
        unsetAuthentication();

        mockMvc.perform(get(LIST_FILE_ENDPOINT).param(LIMIT_PARAMETER, String.valueOf(1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void downloadFile() throws Exception {
        setAuthentication();
        byte[] bytes = FILENAME_ONE.getBytes();

        when(storageService.downloadFile(any())).thenReturn(bytes);

        var result = mockMvc.perform(get(FILE_ENDPOINT).param(FILENAME_PARAMETER, FILENAME_ONE))
                .andExpect(status().isOk())
                .andReturn();
        var resultBytes = result.getResponse().getContentAsByteArray();

        assertArrayEquals(resultBytes, bytes);
    }

    @Test
    void downloadFileException() throws Exception {
        unsetAuthentication();

        mockMvc.perform(get(FILE_ENDPOINT).param(FILENAME_PARAMETER, FILENAME_ONE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadFile() throws Exception {
        setAuthentication();

        Mockito.doNothing().when(storageService).uploadFile(any(), any());

        mockMvc.perform(post(FILE_ENDPOINT).param(FILENAME_PARAMETER, FILENAME_TWO))
                .andExpect(status().isOk());

    }

    @Test
    void uploadFileException() throws Exception {
        unsetAuthentication();

        mockMvc.perform(post(FILE_ENDPOINT).param(FILENAME_PARAMETER, FILENAME_TWO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteFile() throws Exception {
        setAuthentication();

        Mockito.doNothing().when(storageService).deleteFile(any());

        mockMvc.perform(delete(FILE_ENDPOINT).param(FILENAME_PARAMETER, FILENAME_TWO))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFileException() throws Exception {
        unsetAuthentication();

        mockMvc.perform(delete(FILE_ENDPOINT).param(FILENAME_PARAMETER, FILENAME_TWO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void editFileName() throws Exception {
        setAuthentication();

        Mockito.doNothing().when(storageService).editFileName(any(), any());
        var body = objectMapper.writeValueAsString(new FileNameEditRequest(FILENAME_ONE));

        mockMvc.perform(put(FILE_ENDPOINT)
                        .param(FILENAME_PARAMETER, FILENAME_TWO)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void editFileNameException() throws Exception {
        unsetAuthentication();

        var body = objectMapper.writeValueAsString(new FileNameEditRequest(FILENAME_ONE));

        mockMvc.perform(put(FILE_ENDPOINT)
                        .param(FILENAME_PARAMETER, FILENAME_TWO)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private void setAuthentication() {
        Authentication authentication = new UsernamePasswordAuthentication(USERNAME, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void unsetAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}