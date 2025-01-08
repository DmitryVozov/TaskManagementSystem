package ru.vozov.taskmanagamentsystem.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vozov.taskmanagamentsystem.dto.UserUpdateDto;
import ru.vozov.taskmanagamentsystem.dto.RegistrationUserDto;
import ru.vozov.taskmanagamentsystem.exception.*;
import ru.vozov.taskmanagamentsystem.model.Role;
import ru.vozov.taskmanagamentsystem.model.User;
import ru.vozov.taskmanagamentsystem.repository.UserRepository;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements UserDetailsService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    AuthService authService;

    @Autowired
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, @Lazy AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Transactional
    public User save(RegistrationUserDto registrationUserDto, Role role) {
        User user = User.builder()
                .username(registrationUserDto.username())
                .password(passwordEncoder.encode(registrationUserDto.password()))
                .email(registrationUserDto.email())
                .roles(List.of(role))
                .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", id)));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", email)));
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", username)));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    @Transactional
    public User update(UUID id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", id)));

        User authenticatedUser = authService.getAuthenticatedUser();

        if (!authenticatedUser.getId().equals(id) && !authenticatedUser.isAdmin()) {
            throw new AccessDeniedException("Only the account owner and the admin can update the user");
        }

        if (userUpdateDto.username() == null && userUpdateDto.password() == null && userUpdateDto.oldPassword() == null && userUpdateDto.email() == null) {
            throw new NoDataToUpdateException("No data to update user, you can update your username, password and email");
        }

        if (userUpdateDto.password() != null) {
            if (userUpdateDto.oldPassword() == null) {
                throw new ChangePasswordException("To change the password, you must send the old password");
            }

            if (userUpdateDto.password().isBlank()) {
                throw new BlankFieldException("Password cannot be blank");
            }

            try {
                authService.authenticate(user.getEmail(), userUpdateDto.oldPassword());
            }
            catch (BadCredentialsException e) {
                throw new ChangePasswordException("Incorrect old password");
            }

            user.setPassword(passwordEncoder.encode(userUpdateDto.password()));
        }

        if (userUpdateDto.email() != null) {
            if (userRepository.existsByEmail(userUpdateDto.email())) {
                throw new EmailAlreadyExistsException(
                        String.format(
                                "User with email %s already exists",
                                userUpdateDto.email()
                        )
                );
            }

            user.setEmail(userUpdateDto.email());
        }

        if (userUpdateDto.username() != null) {
            if (userUpdateDto.username().isBlank()) {
                throw new BlankFieldException("Username cannot be blank");
            }

            user.setUsername(userUpdateDto.username());
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format("User with id %s not found", id)
            );
        }

        userRepository.deleteById(id);
    }
}
