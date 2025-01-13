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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.vozov.taskmanagamentsystem.dto.ErrorDto;
import ru.vozov.taskmanagamentsystem.dto.UserDto;
import ru.vozov.taskmanagamentsystem.dto.UserUpdateDto;
import ru.vozov.taskmanagamentsystem.model.User;
import ru.vozov.taskmanagamentsystem.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Users", description = "API для работы с пользователями")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Получение пользователя по id",
            description = "Возвращает данные пользователя по уникальному id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") UUID id) {
        User user = userService.findById(id);
        return new ResponseEntity<>(UserDto.convert(user), HttpStatus.OK);
    }

    @Operation(
            summary = "Получение всех пользователей",
            description = "Возвращает список всех пользователей",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный ответ")
            }
    )
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.findAll();
        return new ResponseEntity<>(UserDto.convert(users), HttpStatus.OK);
    }

    @Operation(
            summary = "Обновление пользователя",
            description = "Обновляет пользователя и возвращает обновленные данные, доступно только администратору и самому пользователю",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Переданы некорректные данные для обновления пользователя",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав на обновление пользователя",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") UUID id, @RequestBody @Valid UserUpdateDto userUpdateDto) {
        User user = userService.update(id, userUpdateDto);
        return new ResponseEntity<>(UserDto.convert(user), HttpStatus.OK);
    }

    @Operation(
            summary = "Удаление пользователя",
            description = "Удаляет пользователя, доступно только администратору",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
