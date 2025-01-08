package ru.vozov.taskmanagamentsystem.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vozov.taskmanagamentsystem.dto.TaskCreationDto;
import ru.vozov.taskmanagamentsystem.dto.TaskUpdateDto;
import ru.vozov.taskmanagamentsystem.exception.*;
import ru.vozov.taskmanagamentsystem.model.Task;
import ru.vozov.taskmanagamentsystem.model.User;
import ru.vozov.taskmanagamentsystem.repository.TaskRepository;
import ru.vozov.taskmanagamentsystem.repository.specification.TaskSpecification;
import ru.vozov.taskmanagamentsystem.repository.UserRepository;

import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskService {
    TaskRepository taskRepository;
    UserRepository userRepository;
    AuthService authService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, AuthService authService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public Task findById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", id)));
    }

    @Transactional
    public Task save(TaskCreationDto taskCreationDto) {
        User author = authService.getAuthenticatedUser();

        UUID executorId = taskCreationDto.executorId();
        User executor = null;

        if (executorId != null) {
            executor = userRepository.findById(executorId)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Executor with id %s not exists", executorId)));

            if (!executor.isUser()) {
                throw new IncorrectExecutorRoleException("Executor must has role USER");
            }
        }

        Task task = Task.builder()
                .title(taskCreationDto.title())
                .description(taskCreationDto.description())
                .priority(taskCreationDto.priority())
                .status(Task.Status.TODO)
                .author(author)
                .executor(executor)
                .build();
        return taskRepository.save(task);
    }

    @Transactional
    public Task update(UUID id, TaskUpdateDto taskUpdateDto) {
         Task task = taskRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", id)));

         User user = authService.getAuthenticatedUser();

         if (!actionIsAvailable(user, task)) {
             throw new AccessDeniedException("Only admin and executor of task can update this task.");
         }

         if (user.isAdmin()) {
             if (taskUpdateDto.title() == null && taskUpdateDto.description() == null && taskUpdateDto.priority() == null && taskUpdateDto.status() == null && taskUpdateDto.executorId() == null) {
                 throw new NoDataToUpdateException("No data to update task, you can update title, description, priority, status and executor");
             }

             if (taskUpdateDto.title() != null) {
                 if (taskUpdateDto.title().isBlank()) {
                     throw new BlankFieldException("Title cannot be blank");
                 }

                 task.setTitle(taskUpdateDto.title());
             }

             if (taskUpdateDto.description() != null) {
                 if (taskUpdateDto.description().isBlank()) {
                     throw new BlankFieldException("Description cannot be blank");
                 }

                 task.setDescription(taskUpdateDto.description());
             }

             if (taskUpdateDto.priority() != null) {
                 task.setPriority(taskUpdateDto.priority());
             }

             if (taskUpdateDto.status() != null) {
                 task.setStatus(taskUpdateDto.status());
             }

             if (taskUpdateDto.executorId() != null) {
                 UUID executorId = taskUpdateDto.executorId();
                 User executor = userRepository.findById(executorId)
                         .orElseThrow(() -> new UserNotFoundException(String.format("Executor with id %s not exists", executorId)));

                 if (!executor.isUser()) {
                     throw new IncorrectExecutorRoleException("Executor must has role USER");
                 }

                 task.setExecutor(executor);
             }
         }
         else {
             if (taskUpdateDto.status() == null) {
                 throw new NoDataToUpdateException("No data to update task, you can update status");
             }

             task.setStatus(taskUpdateDto.status());
         }

         return taskRepository.save(task);
    }

    @Transactional
    public void delete(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format("Task with id %s not found", id)
            );
        }
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Task> findTasksByFilter(
           String title,
           String description,
           String priority,
           String status,
           UUID authorId,
           UUID executorId,
           PageRequest pageRequest
    ) {
        Specification<Task> specification = Specification.where(null);

        if (title != null) {
            specification = Specification.where(TaskSpecification.filterByTitle(title));
        }

        if (description != null) {
            specification = specification.and(TaskSpecification.filterByDescription(description));
        }

        if (priority != null) {
            specification = specification.and(TaskSpecification.filterByPriority(priority));
        }

        if (status != null) {
            specification = specification.and(TaskSpecification.filterByStatus(status));
        }

        if (authorId != null) {
            specification = specification.and(TaskSpecification.filterByAuthor(authorId));
        }

        if (executorId != null) {
            specification = specification.and(TaskSpecification.filterByExecutor(executorId));
        }

        return taskRepository.findAll(specification, pageRequest);
    }

    private boolean actionIsAvailable(User user, Task task) {
        User executor = task.getExecutor();
        return (executor != null && user.getId().equals(executor.getId())) || user.isAdmin();
    }
}
