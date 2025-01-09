package ru.vozov.taskmanagamentsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vozov.taskmanagamentsystem.dto.CommentCreationDto;
import ru.vozov.taskmanagamentsystem.dto.CommentUpdateDto;
import ru.vozov.taskmanagamentsystem.exception.AccessDeniedException;
import ru.vozov.taskmanagamentsystem.exception.ResourceNotFoundException;
import ru.vozov.taskmanagamentsystem.exception.TaskNotFoundException;
import ru.vozov.taskmanagamentsystem.model.Comment;
import ru.vozov.taskmanagamentsystem.model.Role;
import ru.vozov.taskmanagamentsystem.model.Task;
import ru.vozov.taskmanagamentsystem.model.User;
import ru.vozov.taskmanagamentsystem.repository.CommentRepository;
import ru.vozov.taskmanagamentsystem.repository.TaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;

    @Mock
    TaskRepository taskRepository;

    @Mock
    AuthService authService;

    @InjectMocks
    CommentService commentService;

    @Test
    void findById_ShouldThrowException_WhenCommentNotExists() {
        UUID id = UUID.randomUUID();

        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> commentService.findById(id));
        verify(commentRepository, times(1)).findById(id);
    }

    @Test
    void findById_ShouldReturnComment_WhenCommentExists() {
        UUID id = UUID.randomUUID();
        Comment comment = Comment.builder()
                .id(id)
                .build();

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        Comment response = commentService.findById(id);
        assertEquals(comment.getId(), response.getId());
        verify(commentRepository, times(1)).findById(id);
    }

    @Test
    void save_ShouldThrowException_WhenTaskNotExists() {
        CommentCreationDto commentCreationDto = new CommentCreationDto("test", UUID.randomUUID());

        when(taskRepository.findById(commentCreationDto.taskId())).thenReturn(Optional.empty());

        assertThrowsExactly(TaskNotFoundException.class, () -> commentService.save(commentCreationDto));
        verify(taskRepository, times(1)).findById(commentCreationDto.taskId());
    }

    @Test
    void save_ShouldThrowException_WhenUserHasNoRights() {
        CommentCreationDto commentCreationDto = new CommentCreationDto("test", UUID.randomUUID());
        User user = User.builder()
                .id(UUID.randomUUID())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        Task task = Task.builder()
                .id(commentCreationDto.taskId())
                .executor(User.builder().id(UUID.randomUUID()).build())
                .build();

        when(taskRepository.findById(commentCreationDto.taskId())).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(user);

        assertThrowsExactly(AccessDeniedException.class, () -> commentService.save(commentCreationDto));
        verify(taskRepository, times(1)).findById(commentCreationDto.taskId());
    }

    @Test
    void delete_ShouldThrowException_WhenCommentNotExists() {
        UUID id = UUID.randomUUID();

        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> commentService.delete(id));
    }

    @Test
    void delete_ShouldThrowException_WhenUserHasNoRights() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();

        when(commentRepository.findById(id)).thenReturn(Optional.of(new Comment()));
        when(authService.getAuthenticatedUser()).thenReturn(user);

        assertThrowsExactly(AccessDeniedException.class, () -> commentService.delete(id));
        verify(commentRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
    }

    @Test
    void delete_ShouldDeleteComment_WhenUserIsAdmin() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_ADMIN")))
                .build();

        when(commentRepository.findById(id)).thenReturn(Optional.of(new Comment()));
        when(authService.getAuthenticatedUser()).thenReturn(user);

        commentService.delete(id);
        verify(commentRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
        verify(commentRepository, times(1)).deleteById(id);
    }

    @Test
    void delete_ShouldDeleteComment_WhenUserIsCommentator() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(UUID.randomUUID())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        Comment comment = Comment.builder()
                .id(id)
                .commentator(user)
                .build();

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(authService.getAuthenticatedUser()).thenReturn(user);

        commentService.delete(id);
        verify(commentRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
        verify(commentRepository, times(1)).deleteById(id);
    }

    @Test
    void update_ShouldThrowException_WhenCommentNotExists() {
        UUID id = UUID.randomUUID();

        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> commentService.update(id, null));
        verify(commentRepository, times(1)).findById(id);
    }

    @Test
    void update_ShouldThrowException_WhenUserHasNoRights() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();

        when(commentRepository.findById(id)).thenReturn(Optional.of(new Comment()));
        when(authService.getAuthenticatedUser()).thenReturn(user);

        assertThrowsExactly(AccessDeniedException.class, () -> commentService.update(id, null));
        verify(commentRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
    }

    @Test
    void update_ShouldReturnComment_WhenUserIsAdmin() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_ADMIN")))
                .build();
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("test");
        Comment comment = Comment.builder()
                .id(id)
                .text(commentUpdateDto.text())
                .build();

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(authService.getAuthenticatedUser()).thenReturn(user);
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment response = commentService.update(id, commentUpdateDto);
        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getText(), response.getText());
        verify(commentRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void update_ShouldReturnComment_WhenUserIsCommentator() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(UUID.randomUUID())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("test");
        Comment comment = Comment.builder()
                .id(id)
                .commentator(user)
                .text(commentUpdateDto.text())
                .build();

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(authService.getAuthenticatedUser()).thenReturn(user);
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment response = commentService.update(id, commentUpdateDto);
        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getText(), response.getText());
        verify(commentRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
        verify(commentRepository, times(1)).save(comment);
    }
}
