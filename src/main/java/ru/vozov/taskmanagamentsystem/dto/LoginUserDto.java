package ru.vozov.taskmanagamentsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record LoginUserDto(
    @Email(message = "incorrect email")
    @NotNull(message = "email is required field")
    String email,

    @NotNull(message = "password is required field")
    String password
) {

}
