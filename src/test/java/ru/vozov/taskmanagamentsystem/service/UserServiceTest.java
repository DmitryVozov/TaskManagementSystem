package ru.vozov.taskmanagamentsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vozov.taskmanagamentsystem.dto.UserUpdateDto;
import ru.vozov.taskmanagamentsystem.exception.*;
import ru.vozov.taskmanagamentsystem.model.Role;
import ru.vozov.taskmanagamentsystem.model.User;
import ru.vozov.taskmanagamentsystem.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .username("user")
                .email("user@gmail.com")
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .executorTasks(List.of())
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User response = userService.findById(id);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(user.getEmail(), response.getEmail());
        assertTrue(response.getExecutorTasks().isEmpty());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void findById_ShouldThrowException_WhenUserNotExists() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> userService.findById(id));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenUserExists() {
        String email = "user@gmail.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertTrue(userService.existsByEmail(email));
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenUserNotExists() {
        String email = "test@gmail.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertFalse(userService.existsByEmail(email));
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_WhenUserExists() {
        String email = "test@gmail.com";
        User user = new User(UUID.randomUUID(), "test", "test", email,List.of(new Role(UUID.randomUUID(), "ROLE_USER")), List.of(), List.of());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        org.springframework.security.core.userdetails.User response = (org.springframework.security.core.userdetails.User) userService.loadUserByUsername(email);

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getUsername());
        assertEquals(user.getPassword(), response.getPassword());
        assertTrue(response.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_USER")));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotExists() {
        String email = "test@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrowsExactly(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void update_ShouldThrowException_WhenUserNotExists() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> userService.update(id, null));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void update_ShouldThrowException_WhenAuthenticatedUserNotEqualsUpdateUser() {
        UUID id = UUID.randomUUID();
        User updateUser = User.builder()
                .id(id)
                .build();
        User authenticatedUser = User.builder()
                .id(UUID.randomUUID())
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(updateUser));
        when(authService.getAuthenticatedUser()).thenReturn(authenticatedUser);

        assertThrowsExactly(AccessDeniedException.class, () -> userService.update(id, null));
        verify(userRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
    }

    @Test
    void update_ShouldThrowException_WhenNoDataToUpdateUser() {
        UUID id = UUID.randomUUID();
        User updateUser = User.builder()
                .id(id)
                .build();
        User authenticatedUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(updateUser));
        when(authService.getAuthenticatedUser()).thenReturn(authenticatedUser);

        assertThrowsExactly(
                NoDataToUpdateException.class,
                () -> userService.update(id, new UserUpdateDto(null, null, null, null))
        );
        verify(userRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
    }

    @Test
    void update_ShouldThrowException_WhenOldPasswordIsNull() {
        UUID id = UUID.randomUUID();
        User updateUser = User.builder()
                .id(id)
                .build();
        User authenticatedUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(updateUser));
        when(authService.getAuthenticatedUser()).thenReturn(authenticatedUser);

        assertThrowsExactly(
                ChangePasswordException.class,
                () -> userService.update(id, new UserUpdateDto(null, null, "test", null))
        );
        verify(userRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
    }

    @Test
    void update_ShouldThrowException_WhenPasswordIsBlack() {
        UUID id = UUID.randomUUID();
        User updateUser = User.builder()
                .id(id)
                .build();
        User authenticatedUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(updateUser));
        when(authService.getAuthenticatedUser()).thenReturn(authenticatedUser);

        assertThrowsExactly(
                BlankFieldException.class,
                () -> userService.update(id, new UserUpdateDto(null, "test", "", null))
        );
        verify(userRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
    }

    @Test
    void update_ShouldUpdatePassword_WhenPasswordIsCorrect() {
        UUID id = UUID.randomUUID();
        User updateUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .executorTasks(List.of())
                .authorTasks(List.of())
                .build();
        User authenticatedUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, "test", "updated", null);

        when(userRepository.findById(id)).thenReturn(Optional.of(updateUser));
        when(authService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(passwordEncoder.encode(userUpdateDto.password())).thenReturn("updated");
        when(userRepository.save(updateUser)).thenReturn(updateUser);

        User response = userService.update(id, userUpdateDto);

        assertNotNull(response);
        assertEquals(id, response.getId());
        verify(userRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
    }

    @Test
    void update_ShouldThrowException_WhenEmailAlreadyExists() {
        UUID id = UUID.randomUUID();
        User updateUser = User.builder()
                .id(id)
                .build();
        User authenticatedUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        String email = "update@gmail.com";

        when(userRepository.findById(id)).thenReturn(Optional.of(updateUser));
        when(authService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrowsExactly(
                EmailAlreadyExistsException.class,
                () -> userService.update(id, new UserUpdateDto(null, null, null, email))
        );
        verify(userRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void update_ShouldUpdateEmail_WhenEmailIsCorrect() {
        UUID id = UUID.randomUUID();
        User updateUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .executorTasks(List.of())
                .authorTasks(List.of())
                .build();
        User authenticatedUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        UserUpdateDto userUpdateDto = new UserUpdateDto(null, null, null, "update@gmail.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(updateUser));
        when(authService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(userRepository.existsByEmail(userUpdateDto.email())).thenReturn(false);
        when(userRepository.save(updateUser)).thenReturn(updateUser);

        User response = userService.update(id, userUpdateDto);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(userUpdateDto.email(), response.getEmail());
        verify(userRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
        verify(userRepository, times(1)).existsByEmail(userUpdateDto.email());
    }

    @Test
    void update_ShouldThrowException_WhenUsernameIsBlank() {
        UUID id = UUID.randomUUID();
        User updateUser = User.builder()
                .id(id)
                .build();
        User authenticatedUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();

        UserUpdateDto userUpdateDto = new UserUpdateDto("", null, null, null);
        when(userRepository.findById(id)).thenReturn(Optional.of(updateUser));
        when(authService.getAuthenticatedUser()).thenReturn(authenticatedUser);

        assertThrowsExactly(
                BlankFieldException.class,
                () -> userService.update(id, userUpdateDto)
        );
        verify(userRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
    }

    @Test
    void update_ShouldUpdateUsername_WhenUsernameIsCorrect() {
        UUID id = UUID.randomUUID();
        User updateUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .executorTasks(List.of())
                .authorTasks(List.of())
                .build();
        User authenticatedUser = User.builder()
                .id(id)
                .roles(List.of(new Role(UUID.randomUUID(), "ROLE_USER")))
                .build();
        UserUpdateDto userUpdateDto = new UserUpdateDto("test", null, null, null);

        when(userRepository.findById(id)).thenReturn(Optional.of(updateUser));
        when(authService.getAuthenticatedUser()).thenReturn(authenticatedUser);
        when(userRepository.save(updateUser)).thenReturn(updateUser);

        User response = userService.update(id, userUpdateDto);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(userUpdateDto.username(), response.getUsername());
        verify(userRepository, times(1)).findById(id);
        verify(authService, times(1)).getAuthenticatedUser();
    }

    @Test
    void findAll_ShouldReturnUsers_WhenUsersExist() {
        Role userRole = new Role(UUID.randomUUID(), "ROLE_USER");
        User test1 = new User(UUID.randomUUID(), "test1", null, "test1@gmail.com", List.of(userRole), List.of(), List.of());
        User test2 = new User(UUID.randomUUID(), "test2", null, "test2@gmail.com", List.of(userRole), List.of(), List.of());
        User test3 = new User(UUID.randomUUID(), "test3", null, "test3@gmail.com", List.of(userRole), List.of(), List.of());
        List<User> expectedUsers = List.of(test1, test2, test3);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> response = userService.findAll();

        assertNotNull(response);
        assertEquals(expectedUsers.size(), response.size());
        assertTrue(response.stream().anyMatch(user -> user.getId().equals(test1.getId())));
        assertTrue(response.stream().anyMatch(user -> user.getId().equals(test2.getId())));
        assertTrue(response.stream().anyMatch(user -> user.getId().equals(test3.getId())));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenUsersNotExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> response = userService.findAll();

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void delete_ShouldDeleteUser_WhenUserExists() {
        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(true);

        userService.delete(id);

        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void delete_ShouldThrowException_WhenUserNotExists() {
        UUID id = UUID.randomUUID();

        when(userRepository.existsById(id)).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class,() -> userService.delete(id));
        verify(userRepository, times(1)).existsById(id);
    }
}
