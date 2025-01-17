package ru.vozov.taskmanagamentsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.vozov.taskmanagamentsystem.dto.UserUpdateDto;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
@Sql(scripts = "classpath:sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    final ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getUser_ShouldReturnUser_WhenUserExists() throws Exception {
        UUID id = UUID.fromString("8f7985de-a578-4419-b93f-ff9d29969b11");

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getUser_ShouldReturn404_WhenUserNotExists() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(String.format("User with id %s not found", id)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getAllUsers_ShouldReturnUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("2bc86005-4208-45da-a289-99c9e8c5d432"))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].email").value("admin@gmail.com"))
                .andExpect(jsonPath("$[1].id").value("8f7985de-a578-4419-b93f-ff9d29969b11"))
                .andExpect(jsonPath("$[1].username").value("test"))
                .andExpect(jsonPath("$[1].email").value("test@gmail.com"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateUser_ShouldReturnUpdatedUser_WhenFieldsAreCorrect() throws Exception {
        String id = "8f7985de-a578-4419-b93f-ff9d29969b11";
        String username = "new_username";
        String email = "new_email@gmail.com";

        UserUpdateDto userUpdateDto = new UserUpdateDto(username, "test", "test1", email);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateUser_ShouldReturn404_WhenUserNotExists() throws Exception {
        String id = UUID.randomUUID().toString();
        String username = "new_username";
        String email = "new_email@gmail.com";

        UserUpdateDto userUpdateDto = new UserUpdateDto(username, "test", "test1", email);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(String.format("User with id %s not found", id)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateUser_ShouldReturn400_WhenEmailIsNotCorrect() throws Exception {
        String id = "8f7985de-a578-4419-b93f-ff9d29969b11";
        String username = "new_username";
        String email = "new_email";

        UserUpdateDto userUpdateDto = new UserUpdateDto(username, "test", "test1", email);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("incorrect email"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateUser_ShouldReturn400_WhenEmailIsBlank() throws Exception {
        String id = "8f7985de-a578-4419-b93f-ff9d29969b11";
        String username = "new_username";
        String email = "";

        UserUpdateDto userUpdateDto = new UserUpdateDto(username, "test", "test1", email);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("Email cannot be blank"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateUser_ShouldReturn400_WhenEmailIsAlreadyExists() throws Exception {
        String id = "8f7985de-a578-4419-b93f-ff9d29969b11";
        String username = "new_username";
        String email = "admin@gmail.com";

        UserUpdateDto userUpdateDto = new UserUpdateDto(username, "test", "test1", email);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value(String.format("User with email %s already exists", email)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateUser_ShouldReturn400_WhenNoDataToUpdate() throws Exception {
        String id = "8f7985de-a578-4419-b93f-ff9d29969b11";
        String username = null;
        String email = null;
        String oldPassword = null;
        String password = null;

        UserUpdateDto userUpdateDto = new UserUpdateDto(username, oldPassword, password, email);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("No data to update user, you can update your username, password and email"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateUser_ShouldReturn400_WhenUsernameIsBlank() throws Exception {
        String id = "8f7985de-a578-4419-b93f-ff9d29969b11";
        String username = " ";
        String email = null;
        String oldPassword = null;
        String password = null;

        UserUpdateDto userUpdateDto = new UserUpdateDto(username, oldPassword, password, email);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("Username cannot be blank"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateUser_ShouldReturn400_WhenOldPasswordIsNoCorrect() throws Exception {
        String id = "8f7985de-a578-4419-b93f-ff9d29969b11";
        String username = null;
        String email = null;
        String oldPassword = "test123";
        String password = "new_password";

        UserUpdateDto userUpdateDto = new UserUpdateDto(username, oldPassword, password, email);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("Incorrect old password"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateUser_ShouldReturn400_WhenOldPasswordIsNullAndPasswordIsNotNull() throws Exception {
        String id = "8f7985de-a578-4419-b93f-ff9d29969b11";
        String username = null;
        String email = null;
        String oldPassword = null;
        String password = "new_password";

        UserUpdateDto userUpdateDto = new UserUpdateDto(username, oldPassword, password, email);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("To change the password, you must send the old password"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void delete_ShouldReturn403_WhenUserHasNoRight() throws Exception {
        String id = "8f7985de-a578-4419-b93f-ff9d29969b11";

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "ADMIN")
    void delete_ShouldReturn404_WhenUserNoExists() throws Exception {
        String id = UUID.randomUUID().toString();

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "ADMIN")
    void delete_ShouldReturn204_WhenUserExists() throws Exception {
        String id = "8f7985de-a578-4419-b93f-ff9d29969b11";

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());
    }
}
