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
import ru.vozov.taskmanagamentsystem.dto.*;
import ru.vozov.taskmanagamentsystem.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "API для аутентификации и регистрации")
public class AuthController {
    AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Регистрация пользователя",
            description = "Создает пользователя и возвращает данные созданного пользователя с токеном доступа",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Переданы некорретные данные для регистрации",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    )
            }
    )
    @PostMapping("/sign-up")
    public ResponseEntity<RegistrationUserResponseDto> signUp(@RequestBody @Valid RegistrationUserDto registrationUserDto) {
        RegistrationUserResponseDto response = authService.signUp(registrationUserDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Возвращает токен доступа по email и паролю",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Переданы некорректные данные для аутентификации",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    )
            }
    )
    @PostMapping("/sign-in")
    public ResponseEntity<JwtDto> signIn(@RequestBody @Valid LoginUserDto loginUserDto) {
        JwtDto response = authService.signIn(loginUserDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
