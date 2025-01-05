package ru.vozov.taskmanagamentsystem.dto;

import ru.vozov.taskmanagamentsystem.model.User;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserDto(
        UUID id,
        String username,
        String email,
        List<TaskDto> tasks
) {
    public static UserDto convert(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                TaskDto.convert(user.isUser() ? user.getExecutorTasks() : user.getAuthorTasks())
        );
    }

    public static List<UserDto> convert(List<User> users) {
        return users.stream().map(UserDto::convert).collect(Collectors.toList());
    }
}
