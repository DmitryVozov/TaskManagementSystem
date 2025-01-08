package ru.vozov.taskmanagamentsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.vozov.taskmanagamentsystem.dto.RegistrationUserDto;
import ru.vozov.taskmanagamentsystem.model.Role;
import ru.vozov.taskmanagamentsystem.repository.RoleRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RoleRepository roleRepository;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    @Transactional
    void createRoles() {
        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");
        roleRepository.save(roleUser);
        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");
        roleRepository.save(roleAdmin);
    }

    @AfterEach
    @Transactional
    void deleteRoles() {
        roleRepository.deleteAll();
    }

    @Test
    void saveUser() throws Exception {
        RegistrationUserDto registrationUserDto = new RegistrationUserDto("test", "test", "test@gmail.com");

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isCreated());
    }
}
