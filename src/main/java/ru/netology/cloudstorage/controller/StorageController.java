package ru.netology.cloudstorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileListResponse;
import ru.netology.cloudstorage.dto.FileNameEditRequest;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.exceptions.BadRequestException;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.service.StorageService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.netology.cloudstorage.configuration.StringConstants.FILENAME_PARAMETER;
import static ru.netology.cloudstorage.configuration.StringConstants.LIMIT_PARAMETER;

@RequiredArgsConstructor
@RestController
public class StorageController {

    private final StorageService storageService;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public List<FileListResponse> getAllFiles(@RequestParam(LIMIT_PARAMETER) Integer limit) {
        var user = getUserFromSecurityContext();
        return storageService.getFileList(user, limit).stream()
                .map(o -> new FileListResponse(o.getFileName(), o.getFileSize()))
                .collect(Collectors.toList());
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam(FILENAME_PARAMETER) String filename) {
        byte[] file = storageService.downloadFile(filename);
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam(FILENAME_PARAMETER) String filename,
                                        MultipartFile file) {
        var user = getUserFromSecurityContext();
        storageService.uploadFile(user, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestParam(FILENAME_PARAMETER) String filename) {
        var user = getUserFromSecurityContext();
        storageService.deleteFile(user, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping(value = "/file")
    public ResponseEntity<?> editFileName(@RequestParam(FILENAME_PARAMETER) String filename,
                                          @RequestBody FileNameEditRequest fileNameEditRequest) {
        storageService.editFileName(filename, fileNameEditRequest);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    private User getUserFromSecurityContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();
        var user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new BadRequestException(String.format("User %s not found", username));
        }
    }
}
