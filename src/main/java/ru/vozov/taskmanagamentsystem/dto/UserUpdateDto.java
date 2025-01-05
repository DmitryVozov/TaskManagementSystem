package ru.vozov.taskmanagamentsystem.dto;

import jakarta.validation.constraints.Email;

public record UserUpdateDto(
        String username,
        String oldPassword,
        String password,
        @Email(message = "incorrect email")
        String email
) {

}
