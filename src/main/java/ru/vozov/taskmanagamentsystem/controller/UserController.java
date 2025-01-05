package ru.vozov.taskmanagamentsystem.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.vozov.taskmanagamentsystem.dto.UserDto;
import ru.vozov.taskmanagamentsystem.dto.UserUpdateDto;
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
    public ResponseEntity<UserDto> getCustomer(@PathVariable("id") UUID id) {
        return userService.findById(id);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllCustomers() {
        return userService.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateCustomer(@PathVariable("id") UUID id, @RequestBody @Valid UserUpdateDto userUpdateDto) {
        return userService.update(id, userUpdateDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") UUID id) {
        return userService.delete(id);
    }
}
