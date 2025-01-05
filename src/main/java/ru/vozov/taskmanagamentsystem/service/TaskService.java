package ru.vozov.taskmanagamentsystem.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vozov.taskmanagamentsystem.dto.TaskDto;
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
    public ResponseEntity<TaskDto> findById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", id)));

        return new ResponseEntity<>(TaskDto.convert(task), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<TaskDto> save(TaskCreationDto taskCreationDto) {
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
                .status(taskCreationDto.status())
                .author(author)
                .executor(executor)
                .build();
        taskRepository.save(task);

        return new ResponseEntity<>(
                TaskDto.convert(task),
                HttpStatus.CREATED
        );
    }

    @Transactional
    public ResponseEntity<TaskDto> update(UUID id, TaskUpdateDto taskUpdateDto) {
         Task task = taskRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException(String.format("Task with id %s not found", id)));

         if (!actionIsAvailable(task, taskUpdateDto)) {
             throw new AccessDeniedException("You can't update this task. Admin can update title, description, priority, executor. Executor can update status.");
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

         taskRepository.save(task);

         return new ResponseEntity<>(
                 TaskDto.convert(task),
                 HttpStatus.OK
         );
    }

    @Transactional
    public ResponseEntity<?> delete(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format("Task with id %s not found", id)
            );
        }
        taskRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Page<TaskDto>> findTasksByFilter(
           String title,
           String description,
           String priority,
           String status,
           UUID authorId,
           UUID executorId,
           PageRequest pageRequest
    ) {
        //TODO
        //Проверить будет ли работать без этой проверки
        if (title == null && description == null && priority == null && status == null && authorId == null && executorId == null) {
                return new ResponseEntity<>(
                    taskRepository.findAll(pageRequest).map(TaskDto::convert),
                    HttpStatus.OK
                );
        }

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

        return new ResponseEntity<>(
                taskRepository.findAll(specification, pageRequest).map(TaskDto::convert),
                HttpStatus.OK
        );
    }

    //Админ может обновлять все, кроме статуса, испольнитель задачи только статус
    private boolean actionIsAvailable(Task task, TaskUpdateDto taskUpdateDto) {
        User user = authService.getAuthenticatedUser();

        return (
            user.getId().equals(task.getExecutor().getId())
            && taskUpdateDto.status() != null
            && taskUpdateDto.title() == null
            && taskUpdateDto.description() == null
            && taskUpdateDto.priority() == null
            && taskUpdateDto.executorId() == null
        )
        || (
            user.isAdmin()
            && (
                taskUpdateDto.title() != null
                || taskUpdateDto.description() != null
                || taskUpdateDto.priority() != null
                || taskUpdateDto.executorId() != null
            )
            && taskUpdateDto.status() == null
        );
    }
}
