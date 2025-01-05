package ru.vozov.taskmanagamentsystem.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vozov.taskmanagamentsystem.dto.JwtDto;
import ru.vozov.taskmanagamentsystem.dto.LoginUserDto;
import ru.vozov.taskmanagamentsystem.dto.RegistrationUserDto;
import ru.vozov.taskmanagamentsystem.exception.EmailAlreadyExistsException;
import ru.vozov.taskmanagamentsystem.exception.SignInException;
import ru.vozov.taskmanagamentsystem.exception.UserNotFoundException;
import ru.vozov.taskmanagamentsystem.model.User;
import ru.vozov.taskmanagamentsystem.repository.UserRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    UserService userService;
    RoleService roleService;
    JwtService jwtService;
    AuthenticationManager authenticationManager;
    UserRepository userRepository;

    @Autowired
    public AuthService(UserService userService, RoleService roleService, JwtService jwtService, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public ResponseEntity<JwtDto> signUp(RegistrationUserDto registrationUserDto) {
        if (userService.existsByEmail(registrationUserDto.email())) {
            throw new EmailAlreadyExistsException(
                    String.format(
                        "User with email %s already exists",
                        registrationUserDto.email()
                    )
            );
        }

        userService.save(registrationUserDto, roleService.findByName("ROLE_USER").orElseThrow());

        return new ResponseEntity<>(
                new JwtDto(
                        jwtService.generateToken(
                            userService.loadUserByUsername(registrationUserDto.email())
                        )
                ),
                HttpStatus.CREATED
        );
    }

    public ResponseEntity<JwtDto> signIn(LoginUserDto loginUserDto) {
        try {
            authenticate(loginUserDto.email(), loginUserDto.password());
        }
        catch (BadCredentialsException e) {
            throw new SignInException("incorrect username or password");
        }

        return ResponseEntity.ok(
                new JwtDto(
                        jwtService.generateToken(
                                userService.loadUserByUsername(loginUserDto.email())
                        )
                )
        );
    }

    public void authenticate(String email, String password) throws BadCredentialsException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", email)));
    }
}
