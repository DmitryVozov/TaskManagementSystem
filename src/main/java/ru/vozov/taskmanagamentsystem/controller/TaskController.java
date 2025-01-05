package ru.vozov.taskmanagamentsystem.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.vozov.taskmanagamentsystem.dto.TaskCreationDto;
import ru.vozov.taskmanagamentsystem.dto.TaskDto;
import ru.vozov.taskmanagamentsystem.dto.TaskUpdateDto;
import ru.vozov.taskmanagamentsystem.service.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {
    TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable UUID id) {
        return taskService.findById(id);
    }

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
        return taskService.findTasksByFilter(title, description, priority, status, authorId, executorId, PageRequest.of(page, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid TaskCreationDto taskCreationDto) {
        return taskService.save(taskCreationDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable("id") UUID id, @RequestBody TaskUpdateDto taskUpdateDto) {
        return taskService.update(id, taskUpdateDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable("id") UUID id) {
        return taskService.delete(id);
    }
}
