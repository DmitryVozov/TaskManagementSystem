package ru.vozov.taskmanagamentsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.vozov.taskmanagamentsystem.dto.LoginUserDto;
import ru.vozov.taskmanagamentsystem.dto.RegistrationUserDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
@Sql(scripts = "classpath:sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    final ObjectMapper mapper = new ObjectMapper();

    @Test
    void signUp_ShouldSaveUser_WhenFieldsAreCorrect() throws Exception {
        String username = "user";
        String email = "user@gmail.com";
        String password = "user";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto(username, password, email);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.username").value(username))
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.id").exists())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void signUp_ShouldReturnError_WhenEmailIsAlreadyExists() throws Exception {
        String username = "user";
        String email = "test@gmail.com";
        String password = "user";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto(username, password, email);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value(String.format("User with email %s already exists", email)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signUp_ShouldReturnError_WhenEmailIsNotCorrect() throws Exception {
        String username = "user";
        String email = "test";
        String password = "user";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto(username, password, email);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("incorrect email"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signUp_ShouldReturnError_WhenEmailIsNull() throws Exception {
        String username = "user";
        String email = null;
        String password = "user";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto(username, password, email);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("email is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signUp_ShouldReturnError_WhenEmailIsBlank() throws Exception {
        String username = "user";
        String email = "";
        String password = "user";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto(username, password, email);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("email is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signUp_ShouldReturnError_WhenUsernameIsNull() throws Exception {
        String username = null;
        String email = "test@gmail.com";
        String password = "user";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto(username, password, email);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("username is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signUp_ShouldReturnError_WhenUsernameIsBlank() throws Exception {
        String username = "";
        String email = "test@gmail.com";
        String password = "user";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto(username, password, email);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("username is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signUp_ShouldReturnError_WhenPasswordIsNull() throws Exception {
        String username = "user";
        String email = "test@gmail.com";
        String password = null;
        RegistrationUserDto registrationUserDto = new RegistrationUserDto(username, password, email);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("password is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signUp_ShouldReturnError_WhenPasswordIsBlank() throws Exception {
        String username = "user";
        String email = "test@gmail.com";
        String password = "  ";
        RegistrationUserDto registrationUserDto = new RegistrationUserDto(username, password, email);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("password is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signIn_ShouldReturnToken_WhenFieldsAreCorrect() throws Exception {
        String email = "test@gmail.com";
        String password = "test";
        LoginUserDto loginUserDto = new LoginUserDto(email, password);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void signIn_ShouldReturnError_WhenEmailIsNotCorrect() throws Exception {
        String email = "test";
        String password = "test";
        LoginUserDto loginUserDto = new LoginUserDto(email, password);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("incorrect email"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signIn_ShouldReturnError_WhenEmailIsNull() throws Exception {
        String email = null;
        String password = "test";
        LoginUserDto loginUserDto = new LoginUserDto(email, password);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("email is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signIn_ShouldReturnError_WhenEmailIsBlank() throws Exception {
        String email = "";
        String password = "test";
        LoginUserDto loginUserDto = new LoginUserDto(email, password);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("email is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signIn_ShouldReturnError_WhenEmailIsNotExists() throws Exception {
        String email = "user123@gmail.com";
        String password = "test";
        LoginUserDto loginUserDto = new LoginUserDto(email, password);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("401"))
                .andExpect(jsonPath("$.message").value("incorrect email or password"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signIn_ShouldReturnError_WhenPasswordIsNull() throws Exception {
        String email = "test@gmail.com";
        String password = null;
        LoginUserDto loginUserDto = new LoginUserDto(email, password);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("password is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signIn_ShouldReturnError_WhenPasswordIsBlank() throws Exception {
        String email = "test@gmail.com";
        String password = " ";
        LoginUserDto loginUserDto = new LoginUserDto(email, password);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("password is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    void signIn_ShouldReturnError_WhenPasswordIsNotCorrect() throws Exception {
        String email = "test@gmail.com";
        String password = "user123";
        LoginUserDto loginUserDto = new LoginUserDto(email, password);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("401"))
                .andExpect(jsonPath("$.message").value("incorrect email or password"))
                .andExpect(jsonPath("$.dateTime").exists());
    }
}
