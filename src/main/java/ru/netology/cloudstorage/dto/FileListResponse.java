package ru.netology.cloudstorage.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileListResponse {
    private String filename;
    private Long size;
}
