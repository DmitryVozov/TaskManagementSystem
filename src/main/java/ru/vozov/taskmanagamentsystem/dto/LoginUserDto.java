package ru.vozov.taskmanagamentsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginUserDto(
    @Email(message = "incorrect email")
    @NotBlank(message = "email is required field")
    String email,

    @NotBlank(message = "password is required field")
    String password
) {

}
