package ru.vozov.taskmanagamentsystem.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vozov.taskmanagamentsystem.dto.JwtDto;
import ru.vozov.taskmanagamentsystem.dto.LoginUserDto;
import ru.vozov.taskmanagamentsystem.dto.RegistrationUserDto;
import ru.vozov.taskmanagamentsystem.dto.RegistrationUserResponseDto;
import ru.vozov.taskmanagamentsystem.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<RegistrationUserResponseDto> signUp(@RequestBody @Valid RegistrationUserDto registrationUserDto) {
        RegistrationUserResponseDto response = authService.signUp(registrationUserDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtDto> signIn(@RequestBody @Valid LoginUserDto loginUserDto) {
        JwtDto response = authService.signIn(loginUserDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
