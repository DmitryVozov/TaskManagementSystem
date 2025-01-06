package ru.vozov.taskmanagamentsystem.dto;

import ru.vozov.taskmanagamentsystem.model.Comment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record CommentDto(
        UUID id,
        String text,
        LocalDateTime createdAt,
        UUID taskId,
        UUID commentatorId
) {
    public static CommentDto convert(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getCreatedAt(),
                comment.getTask().getId(),
                comment.getCommentator() == null ? null : comment.getCommentator().getId()
        );
    }

    public static List<CommentDto> convert(List<Comment> comments) {
        return comments.stream().map(CommentDto::convert).collect(Collectors.toList());
    }
}
