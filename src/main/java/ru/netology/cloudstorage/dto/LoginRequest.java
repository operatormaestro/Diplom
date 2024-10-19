package ru.netology.cloudstorage.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String login;
    private String password;
}
