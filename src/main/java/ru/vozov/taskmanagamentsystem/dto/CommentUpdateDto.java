package ru.vozov.taskmanagamentsystem.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateDto(
        @NotBlank(message = "Text is required field")
        String text
) {
}
