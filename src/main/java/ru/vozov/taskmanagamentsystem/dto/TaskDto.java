package ru.vozov.taskmanagamentsystem.dto;

import ru.vozov.taskmanagamentsystem.model.Task;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public record TaskDto (
    UUID id,
    String title,
    String description,
    Task.Priority priority,
    Task.Status status,
    UUID authorId,
    UUID executorId,
    List<CommentDto> comments
) {
    public static TaskDto convert(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getAuthor() == null ? null : task.getAuthor().getId(),
                task.getExecutor() == null ? null : task.getExecutor().getId(),
                task.getComments() == null ? null : CommentDto.convert(task.getComments())
        );
    }

    public static List<TaskDto> convert(List<Task> tasks) {
        return tasks.stream().map(TaskDto::convert).collect(Collectors.toList());
    }
}
