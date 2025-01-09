package ru.vozov.taskmanagamentsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vozov.taskmanagamentsystem.dto.TaskCreationDto;
import ru.vozov.taskmanagamentsystem.dto.TaskUpdateDto;
import ru.vozov.taskmanagamentsystem.exception.*;
import ru.vozov.taskmanagamentsystem.model.Role;
import ru.vozov.taskmanagamentsystem.model.Task;
import ru.vozov.taskmanagamentsystem.model.User;
import ru.vozov.taskmanagamentsystem.repository.TaskRepository;
import ru.vozov.taskmanagamentsystem.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void findById_ShouldReturnTask_WhenTaskExists() {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, "test", "test", Task.Priority.LOW, Task.Status.TODO, null, null, List.of());

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        Task response = taskService.findById(id);

        assertNotNull(response);
        assertEquals(task.getId(), response.getId());
        assertEquals(task.getTitle(), response.getTitle());
        assertEquals(task.getDescription(), response.getDescription());
        assertEquals(task.getPriority(), response.getPriority());
        assertEquals(task.getStatus(), response.getStatus());
        assertTrue(Objects.isNull(response.getAuthor()));
        assertTrue(Objects.isNull(response.getExecutor()));
        assertTrue(response.getComments().isEmpty());
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void findById_ShouldThrowException_WhenTaskNotExists() {
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> taskService.findById(id));
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void save_ShouldSaveTask_WhenTaskIsCorrect() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .password("admin")
                .email("admin@gmail.com")
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_ADMIN")))
                .build();
        TaskCreationDto taskCreationDto = new TaskCreationDto("test", "test", Task.Priority.LOW, null);

        Task task = Task.builder()
                .title(taskCreationDto.title())
                .description(taskCreationDto.description())
                .priority(taskCreationDto.priority())
                .status(Task.Status.TODO)
                .author(user)
                .executor(null)
                .build();


        when(authService.getAuthenticatedUser()).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task response = taskService.save(taskCreationDto);

        assertNotNull(response);
        assertEquals(task.getTitle(), response.getTitle());
        assertEquals(task.getDescription(), response.getDescription());
        assertEquals(task.getPriority(), response.getPriority());
        assertEquals(task.getStatus(), response.getStatus());
        assertEquals(task.getAuthor().getId(), response.getAuthor().getId());
        assertTrue(Objects.isNull(response.getComments()));
    }

    @Test
    void save_ShouldThrowException_WhenExecutorIsNotExists() {
        User user = new User();
        UUID executorId = UUID.randomUUID();
        TaskCreationDto taskCreationDto = new TaskCreationDto("test", "test", Task.Priority.LOW, executorId);


        when(authService.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.findById(executorId)).thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class, () -> taskService.save(taskCreationDto));
        verify(authService, times(1)).getAuthenticatedUser();
        verify(taskRepository, times(1)).findById(executorId);
    }

    @Test
    void save_ShouldThrowException_WhenExecutorIsAdmin() {
        User user = new User();
        UUID executorId = UUID.randomUUID();
        TaskCreationDto taskCreationDto = new TaskCreationDto("test", "test", Task.Priority.LOW, executorId);
        User executor = User.builder()
                .id(executorId)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_ADMIN")))
                .build();

        when(authService.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.findById(executorId)).thenReturn(Optional.of(executor));

        assertThrowsExactly(IncorrectExecutorRoleException.class, () -> taskService.save(taskCreationDto));
        verify(authService, times(1)).getAuthenticatedUser();
        verify(userRepository, times(1)).findById(executorId);
    }

    @Test
    void update_ShouldThrowException_WhenTaskNotExists() {
        UUID id = UUID.randomUUID();
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto("test", "test", Task.Priority.LOW, Task.Status.TODO, null);

        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> taskService.update(id, taskUpdateDto));
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void update_ShouldThrowException_WhenUserHasNoRightsToUpdate() {
        UUID id = UUID.randomUUID();
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto("test", "test", Task.Priority.LOW, Task.Status.TODO, null);
        Task task = Task.builder()
                .executor(User.builder().id(UUID.randomUUID()).build())
                .build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(user);

        assertThrowsExactly(AccessDeniedException.class, () -> taskService.update(id, taskUpdateDto));
        verify(authService, times(1)).getAuthenticatedUser();
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void update_ShouldThrowException_WhenUserIsAdminAndNoDataToUpdate() {
        UUID id = UUID.randomUUID();
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(null, null, null, null, null);
        Task task = new Task();
        User user = User.builder()
                .id(UUID.randomUUID())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_ADMIN")))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(user);

        assertThrowsExactly(NoDataToUpdateException.class, () -> taskService.update(id, taskUpdateDto));
        verify(authService, times(1)).getAuthenticatedUser();
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void update_ShouldThrowException_WhenUserIsAdminAndTitleIsBlank() {
        UUID id = UUID.randomUUID();
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(" ", null, null, null, null);
        Task task = new Task();
        User user = User.builder()
                .id(UUID.randomUUID())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_ADMIN")))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(user);

        assertThrowsExactly(BlankFieldException.class, () -> taskService.update(id, taskUpdateDto));
        verify(authService, times(1)).getAuthenticatedUser();
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void update_ShouldThrowException_WhenUserIsAdminAndExecutorNotExists() {
        UUID id = UUID.randomUUID();
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(null, null, null, null, UUID.randomUUID());
        Task task = new Task();
        User user = User.builder()
                .id(UUID.randomUUID())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_ADMIN")))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.findById(taskUpdateDto.executorId())).thenReturn(Optional.empty());

        assertThrowsExactly(UserNotFoundException.class, () -> taskService.update(id, taskUpdateDto));
        verify(authService, times(1)).getAuthenticatedUser();
        verify(taskRepository, times(1)).findById(id);
        verify(userRepository, times(1)).findById(taskUpdateDto.executorId());
    }

    @Test
    void update_ShouldThrowException_WhenUserIsAdminAndExecutorIsAdmin() {
        UUID id = UUID.randomUUID();
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(null, null, null, null, UUID.randomUUID());
        Task task = new Task();
        User user = User.builder()
                .id(UUID.randomUUID())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_ADMIN")))
                .build();
        User executor = User.builder()
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_ADMIN")))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.findById(taskUpdateDto.executorId())).thenReturn(Optional.of(executor));

        assertThrowsExactly(IncorrectExecutorRoleException.class, () -> taskService.update(id, taskUpdateDto));
        verify(authService, times(1)).getAuthenticatedUser();
        verify(taskRepository, times(1)).findById(id);
        verify(userRepository, times(1)).findById(taskUpdateDto.executorId());
    }

    @Test
    void update_ShouldReturnTask_WhenUserIsAdminAndFieldsAreCorrect() {
        UUID id = UUID.randomUUID();
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto("test", "test", Task.Priority.LOW, Task.Status.DONE, UUID.randomUUID());
        User executor = User.builder()
                .id(taskUpdateDto.executorId())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        Task task = Task.builder()
                .id(id)
                .title(taskUpdateDto.title())
                .description(taskUpdateDto.description())
                .priority(taskUpdateDto.priority())
                .status(taskUpdateDto.status())
                .executor(executor)
                .build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_ADMIN")))
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.findById(taskUpdateDto.executorId())).thenReturn(Optional.of(executor));
        when(taskRepository.save(task)).thenReturn(task);

        Task response = taskService.update(id, taskUpdateDto);

        assertEquals(task.getId(), response.getId());
        assertEquals(task.getTitle(), response.getTitle());
        assertEquals(task.getDescription(), response.getDescription());
        assertEquals(task.getPriority(), response.getPriority());
        assertEquals(task.getStatus(), response.getStatus());
        assertEquals(task.getExecutor(), response.getExecutor());
        verify(authService, times(1)).getAuthenticatedUser();
        verify(taskRepository, times(1)).findById(id);
        verify(userRepository, times(1)).findById(taskUpdateDto.executorId());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void update_ShouldThrowException_WhenUserIsExecutorAndStatusIsNull() {
        UUID id = UUID.randomUUID();
        UUID executorId = UUID.randomUUID();
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(null, null, null, null, null);
        User user = User.builder()
                .id(executorId)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        User executor = User.builder()
                .id(executorId)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        Task task = Task.builder()
                    .executor(executor)
                    .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(user);

        assertThrowsExactly(NoDataToUpdateException.class, () -> taskService.update(id, taskUpdateDto));
        verify(authService, times(1)).getAuthenticatedUser();
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void update_ShouldReturnTask_WhenUserIsExecutorAndStatusIsNotNull() {
        UUID id = UUID.randomUUID();
        UUID executorId = UUID.randomUUID();
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(null, null, null, Task.Status.DONE, null);
        User user = User.builder()
                .id(executorId)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        User executor = User.builder()
                .id(executorId)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        Task task = Task.builder()
                .id(UUID.randomUUID())
                .executor(executor)
                .status(Task.Status.DONE)
                .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(user);
        when(taskRepository.save(task)).thenReturn(task);

        Task response = taskService.update(id, taskUpdateDto);
        assertEquals(task.getId(), response.getId());
        assertEquals(task.getStatus(), response.getStatus());
        verify(authService, times(1)).getAuthenticatedUser();
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void delete_ShouldThrowException_WhenTaskNotExists() {
        UUID id = UUID.randomUUID();

        when(taskRepository.existsById(id)).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class, () -> taskService.delete(id));
        verify(taskRepository, times(1)).existsById(id);
    }

    @Test
    void delete_ShouldDelete_WhenTaskExists() {
        UUID id = UUID.randomUUID();

        when(taskRepository.existsById(id)).thenReturn(true);

        taskService.delete(id);
        verify(taskRepository, times(1)).existsById(id);
        verify(taskRepository, times(1)).deleteById(id);
    }
}
