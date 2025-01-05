package ru.vozov.taskmanagamentsystem.dto;

import ru.vozov.taskmanagamentsystem.model.Task;

import java.util.UUID;

public record TaskUpdateDto(
        String title,
        String description,
        Task.Priority priority,
        Task.Status status,
        UUID executorId
) {
}
