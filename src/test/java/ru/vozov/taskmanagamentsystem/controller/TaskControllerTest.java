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
import ru.vozov.taskmanagamentsystem.dto.TaskCreationDto;
import ru.vozov.taskmanagamentsystem.dto.TaskUpdateDto;
import ru.vozov.taskmanagamentsystem.model.Task;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
@Sql(scripts = "classpath:sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TaskControllerTest {
    @Autowired
    MockMvc mockMvc;

    final ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getTask_ShouldReturnUser_WhenTaskExists() throws Exception {
        String id = "ea8efca6-8625-4686-8bf8-7c4153d9666e";

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("test"))
                .andExpect(jsonPath("$.description").value("test"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.authorId").value("2bc86005-4208-45da-a289-99c9e8c5d432"))
                .andExpect(jsonPath("$.executorId").value("8f7985de-a578-4419-b93f-ff9d29969b11"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getTask_ShouldReturn404_WhenTaskNotExists() throws Exception {
        String id = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getTasksByFilter_ShouldReturnAllTasks_WhenWithoutFilters() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("ea8efca6-8625-4686-8bf8-7c4153d9666e"))
                .andExpect(jsonPath("$.content[0].title").value("test"))
                .andExpect(jsonPath("$.content[0].description").value("test"))
                .andExpect(jsonPath("$.content[0].priority").value("HIGH"))
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.content[0].authorId").value("2bc86005-4208-45da-a289-99c9e8c5d432"))
                .andExpect(jsonPath("$.content[0].executorId").value("8f7985de-a578-4419-b93f-ff9d29969b11"))
                .andExpect(jsonPath("$.content[1].id").value("b2f1c5b0-31b1-4a15-9ce0-d20300965218"))
                .andExpect(jsonPath("$.content[1].title").value("title"))
                .andExpect(jsonPath("$.content[1].description").value("desc"))
                .andExpect(jsonPath("$.content[1].priority").value("MEDIUM"))
                .andExpect(jsonPath("$.content[1].status").value("TODO"))
                .andExpect(jsonPath("$.content[1].authorId").value("2bc86005-4208-45da-a289-99c9e8c5d432"))
                .andExpect(jsonPath("$.content[1].executorId").isEmpty());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getTasksByFilter_ShouldReturnTask_WhenFilterByAuthorIdAndExecutorId() throws Exception {
        mockMvc.perform(get("/api/tasks?authorId=2bc86005-4208-45da-a289-99c9e8c5d432&executorId=8f7985de-a578-4419-b93f-ff9d29969b11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("ea8efca6-8625-4686-8bf8-7c4153d9666e"))
                .andExpect(jsonPath("$.content[0].title").value("test"))
                .andExpect(jsonPath("$.content[0].description").value("test"))
                .andExpect(jsonPath("$.content[0].priority").value("HIGH"))
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.content[0].authorId").value("2bc86005-4208-45da-a289-99c9e8c5d432"))
                .andExpect(jsonPath("$.content[0].executorId").value("8f7985de-a578-4419-b93f-ff9d29969b11"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getTasksByFilter_ShouldReturnTask_WhenFilterByTitleAndDescriptionAndPriorityAndStatus() throws Exception {
        mockMvc.perform(get("/api/tasks?title=title&description=desc&priority=MEDIUM&status=TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("b2f1c5b0-31b1-4a15-9ce0-d20300965218"))
                .andExpect(jsonPath("$.content[0].title").value("title"))
                .andExpect(jsonPath("$.content[0].description").value("desc"))
                .andExpect(jsonPath("$.content[0].priority").value("MEDIUM"))
                .andExpect(jsonPath("$.content[0].status").value("TODO"))
                .andExpect(jsonPath("$.content[0].authorId").value("2bc86005-4208-45da-a289-99c9e8c5d432"))
                .andExpect(jsonPath("$.content[0].executorId").isEmpty());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void getTasksByFilter_ShouldReturnNothing_WhenFilterByPriority() throws Exception {
        mockMvc.perform(get("/api/tasks?priority=LOW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void createTask_ShouldReturn403_WhenUserHasNoRight() throws Exception {
        String title = "title";
        String description = "desc";
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = null;

        TaskCreationDto taskCreationDto = new TaskCreationDto(title, description, priority, executorId);
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskCreationDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void createTask_ShouldReturn400_WhenTitleIsNull() throws Exception {
        String title = null;
        String description = "desc";
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = null;

        TaskCreationDto taskCreationDto = new TaskCreationDto(title, description, priority, executorId);
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("title is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void createTask_ShouldReturn400_WhenTitleIsBlank() throws Exception {
        String title = " ";
        String description = "desc";
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = null;

        TaskCreationDto taskCreationDto = new TaskCreationDto(title, description, priority, executorId);
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("title is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void createTask_ShouldReturn400_WhenDescriptionIsNull() throws Exception {
        String title = "title";
        String description = null;
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = null;

        TaskCreationDto taskCreationDto = new TaskCreationDto(title, description, priority, executorId);
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("description is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void createTask_ShouldReturn400_WhenDescriptionIsBlank() throws Exception {
        String title = "title";
        String description = "";
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = null;

        TaskCreationDto taskCreationDto = new TaskCreationDto(title, description, priority, executorId);
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("description is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void createTask_ShouldReturn400_WhenPriorityIsNull() throws Exception {
        String title = "title";
        String description = "desc";
        Task.Priority priority = null;
        UUID executorId = null;

        TaskCreationDto taskCreationDto = new TaskCreationDto(title, description, priority, executorId);
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("priority is required field"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void createTask_ShouldReturn400_WhenExecutorNotExists() throws Exception {
        String title = "title";
        String description = "desc";
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = UUID.randomUUID();

        TaskCreationDto taskCreationDto = new TaskCreationDto(title, description, priority, executorId);
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value(String.format("Executor with id %s not exists", executorId)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void createTask_ShouldReturn400_WhenExecutorIsAdmin() throws Exception {
        String title = "title";
        String description = "desc";
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = UUID.fromString("2bc86005-4208-45da-a289-99c9e8c5d432");

        TaskCreationDto taskCreationDto = new TaskCreationDto(title, description, priority, executorId);
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskCreationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("Executor must has role USER"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void createTask_ShouldReturnTask_WhenFieldsAreCorrect() throws Exception {
        String title = "title";
        String description = "desc";
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = UUID.fromString("8f7985de-a578-4419-b93f-ff9d29969b11");

        TaskCreationDto taskCreationDto = new TaskCreationDto(title, description, priority, executorId);
        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskCreationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.priority").value(priority.toString()))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.authorId").value("2bc86005-4208-45da-a289-99c9e8c5d432"))
                .andExpect(jsonPath("$.executorId").value(executorId.toString()));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void updateTask_ShouldReturn404_WhenTaskNotExists() throws Exception {
        String id = UUID.randomUUID().toString();
        String title = "title";
        String description = "desc";
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = UUID.fromString("8f7985de-a578-4419-b93f-ff9d29969b11");
        Task.Status status = Task.Status.DONE;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(title, description, priority, status, executorId);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(String.format("Task with id %s not found", id)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateTask_ShouldReturn403_WhenUserHasNoRight() throws Exception {
        String id = "b2f1c5b0-31b1-4a15-9ce0-d20300965218";
        String title = "title";
        String description = "desc";
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = UUID.fromString("8f7985de-a578-4419-b93f-ff9d29969b11");
        Task.Status status = Task.Status.DONE;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(title, description, priority, status, executorId);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value("403"))
                .andExpect(jsonPath("$.message").value("Only admin and executor of task can update this task."))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void updateTask_ShouldReturn400_WhenUserIsAdminAndNoDataToUpdate() throws Exception {
        String id = "b2f1c5b0-31b1-4a15-9ce0-d20300965218";
        String title = null;
        String description = null;
        Task.Priority priority = null;
        UUID executorId = null;
        Task.Status status = null;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(title, description, priority, status, executorId);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("No data to update task, you can update title, description, priority, status and executor"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void updateTask_ShouldReturn400_WhenTitleIsBlank() throws Exception {
        String id = "b2f1c5b0-31b1-4a15-9ce0-d20300965218";
        String title = " ";
        String description = null;
        Task.Priority priority = null;
        UUID executorId = null;
        Task.Status status = null;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(title, description, priority, status, executorId);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("Title cannot be blank"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void updateTask_ShouldReturn400_WhenDescriptionIsBlank() throws Exception {
        String id = "b2f1c5b0-31b1-4a15-9ce0-d20300965218";
        String title = null;
        String description = " ";
        Task.Priority priority = null;
        UUID executorId = null;
        Task.Status status = null;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(title, description, priority, status, executorId);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("Description cannot be blank"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void updateTask_ShouldReturn400_WhenExecutorNotExists() throws Exception {
        String id = "b2f1c5b0-31b1-4a15-9ce0-d20300965218";
        String title = null;
        String description = null;
        Task.Priority priority = null;
        UUID executorId = UUID.randomUUID();
        Task.Status status = null;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(title, description, priority, status, executorId);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value(String.format("Executor with id %s not exists", executorId)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void updateTask_ShouldReturn400_WhenExecutorIsAdmin() throws Exception {
        String id = "b2f1c5b0-31b1-4a15-9ce0-d20300965218";
        String title = null;
        String description = null;
        Task.Priority priority = null;
        UUID executorId = UUID.fromString("2bc86005-4208-45da-a289-99c9e8c5d432");
        Task.Status status = null;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(title, description, priority, status, executorId);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("Executor must has role USER"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void updateTask_ShouldReturnUpdatedTask_WhenUserIsAdminAndFieldsAreCorrect() throws Exception {
        String id = "b2f1c5b0-31b1-4a15-9ce0-d20300965218";
        String title = "title";
        String description = "desc";
        Task.Priority priority = Task.Priority.LOW;
        UUID executorId = UUID.fromString("8f7985de-a578-4419-b93f-ff9d29969b11");
        Task.Status status = Task.Status.DONE;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(title, description, priority, status, executorId);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.priority").value(priority.toString()))
                .andExpect(jsonPath("$.status").value(status.toString()))
                .andExpect(jsonPath("$.authorId").value("2bc86005-4208-45da-a289-99c9e8c5d432"))
                .andExpect(jsonPath("$.executorId").value(executorId.toString()));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateTask_ShouldReturn400_WhenUserIsExecutorAndNoDataToUpdate() throws Exception {
        String id = "ea8efca6-8625-4686-8bf8-7c4153d9666e";
        String title = null;
        String description = null;
        Task.Priority priority = null;
        UUID executorId = null;
        Task.Status status = null;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(title, description, priority, status, executorId);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("No data to update task, you can update status"))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void updateTask_ShouldReturnUpdatedTask_WhenUserIsExecutorAndStatusIsNotNull() throws Exception {
        String id = "ea8efca6-8625-4686-8bf8-7c4153d9666e";
        String title = null;
        String description = null;
        Task.Priority priority = null;
        UUID executorId = null;
        Task.Status status = Task.Status.DONE;

        TaskUpdateDto taskUpdateDto = new TaskUpdateDto(title, description, priority, status, executorId);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(taskUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("test"))
                .andExpect(jsonPath("$.description").value("test"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value(status.toString()))
                .andExpect(jsonPath("$.authorId").value("2bc86005-4208-45da-a289-99c9e8c5d432"))
                .andExpect(jsonPath("$.executorId").value("8f7985de-a578-4419-b93f-ff9d29969b11"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = "USER")
    void deleteTask_ShouldReturn403_WhenUserHasNoRight() throws Exception {
        String id = "ea8efca6-8625-4686-8bf8-7c4153d9666e";

        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void deleteTask_ShouldReturn404_WhenTaskNotExists() throws Exception {
        String id = UUID.randomUUID().toString();

        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.message").value(String.format("Task with id %s not found", id)))
                .andExpect(jsonPath("$.dateTime").exists());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "ADMIN")
    void deleteTask_ShouldReturn204_WhenTaskExists() throws Exception {
        String id = "ea8efca6-8625-4686-8bf8-7c4153d9666e";

        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNoContent());
    }
}
