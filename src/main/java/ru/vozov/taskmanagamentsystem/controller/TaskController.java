package ru.vozov.taskmanagamentsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.vozov.taskmanagamentsystem.dto.ErrorDto;
import ru.vozov.taskmanagamentsystem.dto.TaskCreationDto;
import ru.vozov.taskmanagamentsystem.dto.TaskDto;
import ru.vozov.taskmanagamentsystem.dto.TaskUpdateDto;
import ru.vozov.taskmanagamentsystem.model.Task;
import ru.vozov.taskmanagamentsystem.service.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Tasks", description = "API для работы с задачами")
public class TaskController {
    TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
            summary = "Получение задачи по id",
            description = "Возвращает данные задачи по уникальному id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Задача не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable UUID id) {
        Task task = taskService.findById(id);
        return new ResponseEntity<>(TaskDto.convert(task), HttpStatus.OK);
    }

    @Operation(
            summary = "Получение всех задач",
            description = "Возвращает список задач с фильтрацией по параметрам и пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный ответ")
            }
    )
    @GetMapping
    public ResponseEntity<Page<TaskDto>> getTasksByFilter(
          @RequestParam(value = "title", required = false) String title,
          @RequestParam(value = "description", required = false) String description,
          @RequestParam(value = "priority", required = false) String priority,
          @RequestParam(value = "status", required = false) String status,
          @RequestParam(value = "authorId", required = false) UUID authorId,
          @RequestParam(value = "executorId", required = false) UUID executorId,
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "pageSize", defaultValue = "20") int pageSize
    ) {
        Page<Task> tasks = taskService.findTasksByFilter(title, description, priority, status, authorId, executorId, PageRequest.of(page, pageSize));
        return new ResponseEntity<>(tasks.map(TaskDto::convert), HttpStatus.OK);
    }

    @Operation(
            summary = "Создание задачи",
            description = "Создает задачу и возвращает данные по созданной задаче, доступно только администратору",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Переданы некорректные данные для создания задачи",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав на создание задачи"
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid TaskCreationDto taskCreationDto) {
        Task task = taskService.save(taskCreationDto);
        return new ResponseEntity<>(TaskDto.convert(task), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Обновление задачи по id",
            description = "Обновляет задачу по уникальному id и возвращает обновленные данные, доступно администратору и исполнителю задачи, администратор может обновлять все данные, испольнитель только статус",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Переданы некорректные данные для обновления задачи",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав на обновление задачи",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Задача не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable("id") UUID id, @RequestBody TaskUpdateDto taskUpdateDto) {
        Task task = taskService.update(id, taskUpdateDto);
        return new ResponseEntity<>(TaskDto.convert(task), HttpStatus.OK);
    }

    @Operation(
            summary = "Удаление задачи по id",
            description = "Удаляет задачу по уникальному id, доступно только администратору",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав на удаление задачи"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Задача не найдена",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable("id") UUID id) {
        taskService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
