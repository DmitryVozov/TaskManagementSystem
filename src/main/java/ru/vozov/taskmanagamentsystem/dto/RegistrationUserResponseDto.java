package ru.vozov.taskmanagamentsystem.dto;

public record RegistrationUserResponseDto (
    UserDto user,
    String token
) {
}
