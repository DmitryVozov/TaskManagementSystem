package ru.vozov.taskmanagamentsystem.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vozov.taskmanagamentsystem.dto.*;
import ru.vozov.taskmanagamentsystem.exception.EmailAlreadyExistsException;
import ru.vozov.taskmanagamentsystem.exception.SignInException;
import ru.vozov.taskmanagamentsystem.model.User;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    UserService userService;
    RoleService roleService;
    JwtService jwtService;
    AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserService userService, RoleService roleService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.roleService = roleService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public RegistrationUserResponseDto signUp(RegistrationUserDto registrationUserDto) {
        if (userService.existsByEmail(registrationUserDto.email())) {
            throw new EmailAlreadyExistsException(
                    String.format(
                        "User with email %s already exists",
                        registrationUserDto.email()
                    )
            );
        }

        User user = userService.save(registrationUserDto, roleService.findByName("ROLE_USER").orElseThrow());
        return new RegistrationUserResponseDto(
                UserDto.convert(user),
                jwtService.generateToken(
                        userService.loadUserByUsername(registrationUserDto.email())
                )
        );
    }

    public JwtDto signIn(LoginUserDto loginUserDto) {
        try {
            authenticate(loginUserDto.email(), loginUserDto.password());
        }
        catch (BadCredentialsException e) {
            throw new SignInException("incorrect username or password");
        }

        return new JwtDto(
            jwtService.generateToken(
                    userService.loadUserByUsername(loginUserDto.email())
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
        return userService.findByEmail(email);
    }
}
