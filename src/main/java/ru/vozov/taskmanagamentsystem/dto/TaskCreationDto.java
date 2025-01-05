package ru.vozov.taskmanagamentsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.vozov.taskmanagamentsystem.model.Task;

import java.util.UUID;

public record TaskCreationDto(
        @NotBlank(message = "title is required field")
        String title,

        @NotBlank(message = "description is required field")
        String description,

        @NotNull(message = "priority is required field")
        Task.Priority priority,

        @NotNull(message = "status is required field")
        Task.Status status,

        UUID executorId
) {
}
