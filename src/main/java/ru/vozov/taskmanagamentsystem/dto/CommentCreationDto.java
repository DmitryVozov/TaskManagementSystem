package ru.vozov.taskmanagamentsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CommentCreationDto(
        @NotBlank(message = "text is required field")
        String text,
        @NotNull(message = "taskId is required field")
        UUID taskId
) {
}
