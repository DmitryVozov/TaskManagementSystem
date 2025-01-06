package ru.vozov.taskmanagamentsystem.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vozov.taskmanagamentsystem.dto.CommentCreationDto;
import ru.vozov.taskmanagamentsystem.dto.CommentDto;
import ru.vozov.taskmanagamentsystem.dto.CommentUpdateDto;
import ru.vozov.taskmanagamentsystem.exception.AccessDeniedException;
import ru.vozov.taskmanagamentsystem.exception.ResourceNotFoundException;
import ru.vozov.taskmanagamentsystem.exception.TaskNotFoundException;
import ru.vozov.taskmanagamentsystem.model.Comment;
import ru.vozov.taskmanagamentsystem.model.Task;
import ru.vozov.taskmanagamentsystem.model.User;
import ru.vozov.taskmanagamentsystem.repository.CommentRepository;
import ru.vozov.taskmanagamentsystem.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    TaskRepository taskRepository;
    AuthService authService;

    @Autowired
    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, AuthService authService) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<CommentDto> findById(UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Comment with id %s not found", id)));

        return new ResponseEntity<>(CommentDto.convert(comment), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<CommentDto> save(CommentCreationDto commentCreationDto) {
        Task task = taskRepository.findById(commentCreationDto.taskId())
                .orElseThrow(() -> new TaskNotFoundException(String.format("Task with id %s not found",commentCreationDto.taskId())));

        if (actionIsUnavailable(task.getExecutor()) ) {
            throw new AccessDeniedException("Only admin or executor of task can leave comment");
        }

        Comment comment = Comment.builder()
                .text(commentCreationDto.text())
                .createdAt(LocalDateTime.now())
                .task(task)
                .commentator(authService.getAuthenticatedUser())
                .build();

        commentRepository.save(comment);

        return new ResponseEntity<>(CommentDto.convert(comment), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> delete(UUID id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Comment with id %s not found", id)));

        if (actionIsUnavailable(comment.getCommentator())) {
            throw new AccessDeniedException("Only admin or creator of comment can delete the comment");
        }

        commentRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Transactional
    public ResponseEntity<CommentDto> update(UUID id, CommentUpdateDto commentUpdateDto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Comment with id %s not found", id)));

        if (actionIsUnavailable(comment.getCommentator()) ) {
            throw new AccessDeniedException("Only admin or creator of comment can edit the comment");
        }

        comment.setText(commentUpdateDto.text());
        commentRepository.save(comment);

        return new ResponseEntity<>(
                CommentDto.convert(comment),
                HttpStatus.OK
        );
    }

    private boolean actionIsUnavailable(User owner) {
        User authenticatedUser = authService.getAuthenticatedUser();
        //Оставлять комментарии может админ и испольнитель задачи
        //Обновлять и удалять комментарий может админ и автор комментария
        return !(authenticatedUser.isAdmin() || (owner != null && authenticatedUser.getId().equals(owner.getId())));
    }
}
