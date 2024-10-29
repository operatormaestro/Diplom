package ru.netology.cloudstorage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileNameEditRequest;
import ru.netology.cloudstorage.entity.Storage;
import ru.netology.cloudstorage.entity.User;
import ru.netology.cloudstorage.exceptions.BadRequestException;
import ru.netology.cloudstorage.exceptions.InternalServerException;
import ru.netology.cloudstorage.repository.StorageRepository;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StorageService {

    private final UserRepository userRepository;
    private final StorageRepository storageRepository;

    public void uploadFile(User user, String filename, MultipartFile file) {
        try {
            storageRepository.save(new Storage(filename, file.getSize(), file.getBytes(), user));
            log.info("File: " + filename + " uploaded. Size: " + file.getSize());
        } catch (Exception e) {
            throw new InternalServerException("Error upload file", e.getMessage());
        }
    }

    public List<Storage> getFileList(User user, Integer limit) {
        try {
            List<Storage> list = storageRepository.findAllByUser(user, Limit.of(limit));
            log.info("Generated a list of size: " + list.size());
            return list;
        } catch (Exception e) {
            throw new InternalServerException("Error getting file list", e.getMessage());
        }
    }

    public byte[] downloadFile(String filename) {
        try {
            var file = getFileByFilename(filename);
            log.info("File: " + filename + " downloaded.");
            return file.getFileContent();
        } catch (Exception e) {
            throw new InternalServerException("Error download file", e.getMessage());
        }
    }

    @Transactional
    public void deleteFile(User user, String filename) {
        try {
            getFileByFilename(filename);
            storageRepository.deleteByUserAndFileName(user, filename);
            log.info("File: " + filename + " deleted.");
        } catch (Exception e) {
            throw new InternalServerException(String.format("Error deleting %s", filename), e.getMessage());
        }
    }

    public void editFileName(String filename, FileNameEditRequest fileNameEditRequest) {
        try {
            var file = getFileByFilename(filename);
            file.setFileName(fileNameEditRequest.getFilename());
            storageRepository.save(file);
            log.info("File: " + filename + " renamed.");
        } catch (Exception e) {
            throw new InternalServerException(String.format("Error editing %s", filename), e.getMessage());
        }
    }

    private Storage getFileByFilename(String filename) {
        User user;
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();
        var userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            throw new BadRequestException(String.format("User %s not found", username));
        }
        var file = storageRepository.findByUserAndFileName(user, filename);
        if (file.isPresent()) {
            return file.get();
        } else {
            throw new BadRequestException(String.format("File %s not found", filename));
        }
    }
}
