package ru.vozov.taskmanagamentsystem.controller;

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
import ru.vozov.taskmanagamentsystem.dto.TaskCreationDto;
import ru.vozov.taskmanagamentsystem.dto.TaskDto;
import ru.vozov.taskmanagamentsystem.dto.TaskUpdateDto;
import ru.vozov.taskmanagamentsystem.model.Task;
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
        Task task = taskService.findById(id);
        return new ResponseEntity<>(TaskDto.convert(task), HttpStatus.OK);
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
        Page<Task> tasks = taskService.findTasksByFilter(title, description, priority, status, authorId, executorId, PageRequest.of(page, pageSize));
        return new ResponseEntity<>(tasks.map(TaskDto::convert), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid TaskCreationDto taskCreationDto) {
        Task task = taskService.save(taskCreationDto);
        return new ResponseEntity<>(TaskDto.convert(task), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable("id") UUID id, @RequestBody TaskUpdateDto taskUpdateDto) {
        Task task = taskService.update(id, taskUpdateDto);
        return new ResponseEntity<>(TaskDto.convert(task), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable("id") UUID id) {
        taskService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
