package ru.vozov.taskmanagamentsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrationUserDto(
    @NotBlank(message = "username is required field")
    String username,

    @NotBlank(message = "password is required field")
    String password,

    @NotNull(message = "email is required field")
    @Email(message = "incorrect email")
    String email
) {
}
