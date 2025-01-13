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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vozov.taskmanagamentsystem.dto.CommentCreationDto;
import ru.vozov.taskmanagamentsystem.dto.CommentDto;
import ru.vozov.taskmanagamentsystem.dto.CommentUpdateDto;
import ru.vozov.taskmanagamentsystem.dto.ErrorDto;
import ru.vozov.taskmanagamentsystem.model.Comment;
import ru.vozov.taskmanagamentsystem.service.CommentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Comments", description = "API для работы с комментариями")
public class CommentController {
    CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(
            summary = "Получение комментария по id",
            description = "Возвращает данные комментария по уникальному id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Комментарий не найден",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable("id") UUID id) {
        Comment comment = commentService.findById(id);
        return new ResponseEntity<>(CommentDto.convert(comment), HttpStatus.OK);
    }

    @Operation(
            summary = "Создание комментария",
            description = "Создает комментарий для переданной задачи и возвращает данные по созданному комментарию",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Переданы некорректные данные для создания комментария",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав на создание комментария",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentCreationDto commentCreationDto) {
        Comment comment = commentService.save(commentCreationDto);
        return new ResponseEntity<>(CommentDto.convert(comment), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Удаление комментария по id",
            description = "Удаляет комментарий по уникальному id",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав на удаление комментария",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Комментарий не найден",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") UUID id) {
        commentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Обновление комментария по id",
            description = "Обновляет текст комментария по уникальному id и возвращает обновленные данные",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Переданы некорректные данные для обновления комментария",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав на обновление комментария",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Комментарий не найден",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("id") UUID id, @RequestBody @Valid CommentUpdateDto commentUpdateDto) {
        Comment comment = commentService.update(id, commentUpdateDto);
        return new ResponseEntity<>(CommentDto.convert(comment), HttpStatus.OK);
    }
}
