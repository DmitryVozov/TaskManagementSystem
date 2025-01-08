package ru.vozov.taskmanagamentsystem.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.vozov.taskmanagamentsystem.dto.UserDto;
import ru.vozov.taskmanagamentsystem.dto.UserUpdateDto;
import ru.vozov.taskmanagamentsystem.model.User;
import ru.vozov.taskmanagamentsystem.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") UUID id) {
        User user = userService.findById(id);
        return new ResponseEntity<>(UserDto.convert(user), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.findAll();
        return new ResponseEntity<>(UserDto.convert(users), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") UUID id, @RequestBody @Valid UserUpdateDto userUpdateDto) {
        User user = userService.update(id, userUpdateDto);
        return new ResponseEntity<>(UserDto.convert(user), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
