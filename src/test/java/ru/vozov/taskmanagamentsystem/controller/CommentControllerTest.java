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
import ru.vozov.taskmanagamentsystem.dto.CommentCreationDto;
import ru.vozov.taskmanagamentsystem.dto.CommentUpdateDto;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
@Sql(scripts = "classpath:sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;

    final ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getComment_ShouldReturn404_WhenCommentNotExists() throws Exception {
        String id = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/comments/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(String.format("Comment with id %s not found", id)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getComment_ShouldReturnComment_WhenCommentExists() throws Exception {
        String id = "d061c985-a0f6-420f-b49f-a85971fb27e4";

        mockMvc.perform(get("/api/comments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value("I am almost done"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.taskId").value("ea8efca6-8625-4686-8bf8-7c4153d9666e"))
                .andExpect(jsonPath("$.commentatorId").value("8f7985de-a578-4419-b93f-ff9d29969b11"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void createComment_ShouldReturn400_WhenTextIsNull() throws Exception {
        String text = null;
        UUID taskId = UUID.fromString("ea8efca6-8625-4686-8bf8-7c4153d9666e");
        CommentCreationDto commentCreationDto = new CommentCreationDto(text, taskId);

        mockMvc.perform(post("/api/comments")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("text is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void createComment_ShouldReturn400_WhenTextIsBlank() throws Exception {
        String text = "    ";
        UUID taskId = UUID.fromString("ea8efca6-8625-4686-8bf8-7c4153d9666e");
        CommentCreationDto commentCreationDto = new CommentCreationDto(text, taskId);

        mockMvc.perform(post("/api/comments")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("text is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void createComment_ShouldReturn400_WhenTaskIdIsNull() throws Exception {
        String text = "123";
        UUID taskId = null;
        CommentCreationDto commentCreationDto = new CommentCreationDto(text, taskId);

        mockMvc.perform(post("/api/comments")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("taskId is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void createComment_ShouldReturn400_WhenTaskNotExists() throws Exception {
        String text = "123";
        UUID taskId = UUID.randomUUID();
        CommentCreationDto commentCreationDto = new CommentCreationDto(text, taskId);

        mockMvc.perform(post("/api/comments")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value(String.format("Task with id %s not found", taskId)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void createComment_ShouldReturn403_WhenUserHasNoRight() throws Exception {
        String text = "123";
        UUID taskId = UUID.fromString("b2f1c5b0-31b1-4a15-9ce0-d20300965218");
        CommentCreationDto commentCreationDto = new CommentCreationDto(text, taskId);

        mockMvc.perform(post("/api/comments")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentCreationDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("403"))
                .andExpect(jsonPath("$.message").value("Only admin or executor of task can leave comment"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void createComment_ShouldReturnComment_WhenFieldsAreCorrect() throws Exception {
        String text = "123";
        UUID taskId = UUID.fromString("ea8efca6-8625-4686-8bf8-7c4153d9666e");
        CommentCreationDto commentCreationDto = new CommentCreationDto(text, taskId);

        mockMvc.perform(post("/api/comments")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentCreationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.taskId").value(taskId.toString()))
                .andExpect(jsonPath("$.commentatorId").value("8f7985de-a578-4419-b93f-ff9d29969b11"));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void createComment_ShouldReturnComment_WhenUserIsAdmin() throws Exception {
        String text = "123";
        UUID taskId = UUID.fromString("ea8efca6-8625-4686-8bf8-7c4153d9666e");
        CommentCreationDto commentCreationDto = new CommentCreationDto(text, taskId);

        mockMvc.perform(post("/api/comments")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentCreationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.taskId").value(taskId.toString()))
                .andExpect(jsonPath("$.commentatorId").value("2bc86005-4208-45da-a289-99c9e8c5d432"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void deleteComment_ShouldReturn404_WhenCommentNotExists() throws Exception {
        String id = UUID.randomUUID().toString();

        mockMvc.perform(delete("/api/comments/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(String.format("Comment with id %s not found", id)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void deleteComment_ShouldReturn403_WhenUserHasNoRight() throws Exception {
        String id = "8ee7714a-f747-4a39-b589-9bcffc6db573";

        mockMvc.perform(delete("/api/comments/{id}", id))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("403"))
                .andExpect(jsonPath("$.message").value("Only admin or creator of comment can delete the comment"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void deleteComment_ShouldReturn204_WhenUserIsAdminAndNotCommentator() throws Exception {
        String id = "d061c985-a0f6-420f-b49f-a85971fb27e4";

        mockMvc.perform(delete("/api/comments/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void deleteComment_ShouldReturn204_WhenUserIsCommentator() throws Exception {
        String id = "d061c985-a0f6-420f-b49f-a85971fb27e4";

        mockMvc.perform(delete("/api/comments/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateComment_ShouldReturn404_WhenCommentNotExists() throws Exception {
        String id = UUID.randomUUID().toString();
        String text = "комментарий";
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(text);

        mockMvc.perform(put("/api/comments/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(String.format("Comment with id %s not found", id)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateComment_ShouldReturn400_WhenTextIsNull() throws Exception {
        String id = "d061c985-a0f6-420f-b49f-a85971fb27e4";
        String text = null;
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(text);

        mockMvc.perform(put("/api/comments/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("Text is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateComment_ShouldReturn400_WhenTextIsBlank() throws Exception {
        String id = "d061c985-a0f6-420f-b49f-a85971fb27e4";
        String text = "     ";
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(text);

        mockMvc.perform(put("/api/comments/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("Text is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateComment_ShouldReturn403_WhenUserIsNotCommentator() throws Exception {
        String id = "8ee7714a-f747-4a39-b589-9bcffc6db573";
        String text = "комментарий";
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(text);

        mockMvc.perform(put("/api/comments/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("403"))
                .andExpect(jsonPath("$.message").value("Only admin or creator of comment can edit the comment"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void updateComment_ShouldReturnUpdatedComment_WhenUserIsAdminAndNotCommentator() throws Exception {
        String id = "d061c985-a0f6-420f-b49f-a85971fb27e4";
        String text = "комментарий";
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(text);

        mockMvc.perform(put("/api/comments/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.taskId").value("ea8efca6-8625-4686-8bf8-7c4153d9666e"))
                .andExpect(jsonPath("$.commentatorId").value("8f7985de-a578-4419-b93f-ff9d29969b11"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateComment_ShouldReturnUpdatedComment_WhenUserIsCommentator() throws Exception {
        String id = "d061c985-a0f6-420f-b49f-a85971fb27e4";
        String text = "комментарий";
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto(text);

        mockMvc.perform(put("/api/comments/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.taskId").value("ea8efca6-8625-4686-8bf8-7c4153d9666e"))
                .andExpect(jsonPath("$.commentatorId").value("8f7985de-a578-4419-b93f-ff9d29969b11"));
    }
}
