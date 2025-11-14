package com.ecommerce.davivienda.controller.user;

import com.ecommerce.davivienda.models.Response;
import com.ecommerce.davivienda.models.user.PasswordChangeAuthenticatedRequest;
import com.ecommerce.davivienda.models.user.PasswordRecoveryRequest;
import com.ecommerce.davivienda.models.user.UserRequest;
import com.ecommerce.davivienda.models.user.UserResponse;
import com.ecommerce.davivienda.models.user.UserUpdateRequest;
import com.ecommerce.davivienda.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests unitarios para UserController.
 * Valida todos los endpoints y casos de uso del controlador de usuarios.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController - Tests Unitarios")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/v1/users/create - Crear usuario exitosamente")
    void testCreateUser_Success() throws Exception {
        // Arrange
        UserRequest request = UserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nombre("John")
                .apellido("Doe")
                .documentType("CC")
                .documentNumber("123456789")
                .roles(Arrays.asList("Cliente"))
                .build();

        Response<String> mockResponse = Response.<String>builder()
                .failure(false)
                .code(201)
                .message("Usuario creado exitosamente")
                .body("Usuario creado exitosamente")
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        when(userService.createUser(any(UserRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Usuario creado exitosamente"));

        verify(userService).createUser(any(UserRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/users/update - Actualizar usuario con ID exitosamente")
    void testUpdateUser_WithId_Success() throws Exception {
        // Arrange
        UserUpdateRequest request = UserUpdateRequest.builder()
                .id(1)
                .nombre("Jane")
                .apellido("Doe")
                .build();

        Response<String> mockResponse = Response.<String>builder()
                .failure(false)
                .code(200)
                .message("Usuario actualizado exitosamente")
                .body("Usuario actualizado exitosamente")
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        when(userService.updateUser(eq(1), any(UserUpdateRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Usuario actualizado exitosamente"));

        verify(userService).updateUser(eq(1), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/users/update - Actualizar usuario autenticado sin ID")
    void testUpdateUser_WithoutId_Success() throws Exception {
        // Arrange
        UserUpdateRequest request = UserUpdateRequest.builder()
                .nombre("Jane")
                .apellido("Doe")
                .build();

        Response<String> mockResponse = Response.<String>builder()
                .failure(false)
                .code(200)
                .message("Usuario actualizado exitosamente")
                .body("Usuario actualizado exitosamente")
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        when(userService.updateUser(eq(null), any(UserUpdateRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).updateUser(eq(null), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/change-password - Recuperar contraseña exitosamente")
    void testRecoverPassword_Success() throws Exception {
        // Arrange
        PasswordRecoveryRequest request = PasswordRecoveryRequest.builder()
                .email("test@example.com")
                .newPassword("newPassword123")
                .envioCorreo(true)
                .build();

        Response<String> mockResponse = Response.<String>builder()
                .failure(false)
                .code(200)
                .message("Contraseña actualizada y correo enviado exitosamente")
                .body("Contraseña actualizada y correo enviado exitosamente")
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        when(userService.recoverPassword(anyString(), anyString(), any(Boolean.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Contraseña actualizada y correo enviado exitosamente"));

        verify(userService).recoverPassword(eq("test@example.com"), eq("newPassword123"), eq(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/change-password - Recuperar contraseña sin envío de correo")
    void testRecoverPassword_WithoutEmail_Success() throws Exception {
        // Arrange
        PasswordRecoveryRequest request = PasswordRecoveryRequest.builder()
                .email("test@example.com")
                .newPassword("newPassword123")
                .envioCorreo(false)
                .build();

        Response<String> mockResponse = Response.<String>builder()
                .failure(false)
                .code(200)
                .message("Contraseña actualizada exitosamente")
                .body("Contraseña actualizada exitosamente")
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        when(userService.recoverPassword(anyString(), anyString(), any(Boolean.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).recoverPassword(eq("test@example.com"), eq("newPassword123"), eq(false));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/change-password-authenticated - Cambiar contraseña autenticado")
    void testChangePasswordAuthenticated_Success() throws Exception {
        // Arrange
        PasswordChangeAuthenticatedRequest request = PasswordChangeAuthenticatedRequest.builder()
                .email("test@example.com")
                .newPassword("newPassword123")
                .build();

        Response<String> mockResponse = Response.<String>builder()
                .failure(false)
                .code(200)
                .message("Contraseña actualizada exitosamente")
                .body("Contraseña actualizada exitosamente")
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        when(userService.changePasswordAuthenticated(anyString(), anyString())).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/users/change-password-authenticated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Contraseña actualizada exitosamente"));

        verify(userService).changePasswordAuthenticated(eq("test@example.com"), eq("newPassword123"));
    }

    @Test
    @DisplayName("GET /api/v1/users/all - Obtener todos los usuarios exitosamente")
    void testGetAllUsers_Success() throws Exception {
        // Arrange
        UserResponse user1 = UserResponse.builder()
                .usuarioId(1)
                .email("user1@example.com")
                .nombre("John")
                .apellido("Doe")
                .build();

        UserResponse user2 = UserResponse.builder()
                .usuarioId(2)
                .email("user2@example.com")
                .nombre("Jane")
                .apellido("Smith")
                .build();

        List<UserResponse> users = Arrays.asList(user1, user2);

        Response<List<UserResponse>> mockResponse = Response.<List<UserResponse>>builder()
                .failure(false)
                .code(200)
                .message("Usuarios encontrados exitosamente")
                .body(users)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        when(userService.getAllUsers()).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body").isArray())
                .andExpect(jsonPath("$.body[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$.body[1].email").value("user2@example.com"));

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Obtener usuario por ID exitosamente")
    void testGetUserById_Success() throws Exception {
        // Arrange
        UserResponse userResponse = UserResponse.builder()
                .usuarioId(1)
                .email("test@example.com")
                .nombre("John")
                .apellido("Doe")
                .build();

        Response<UserResponse> mockResponse = Response.<UserResponse>builder()
                .failure(false)
                .code(200)
                .message("Usuario encontrado exitosamente")
                .body(userResponse)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        when(userService.getUserById(1)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body.email").value("test@example.com"))
                .andExpect(jsonPath("$.body.nombre").value("John"));

        verify(userService).getUserById(1);
    }

    @Test
    @DisplayName("GET /api/v1/users/me - Obtener usuario autenticado exitosamente")
    void testGetAuthenticatedUser_Success() throws Exception {
        // Arrange
        UserResponse userResponse = UserResponse.builder()
                .usuarioId(1)
                .email("authenticated@example.com")
                .nombre("John")
                .apellido("Doe")
                .build();

        Response<UserResponse> mockResponse = Response.<UserResponse>builder()
                .failure(false)
                .code(200)
                .message("Usuario autenticado encontrado exitosamente")
                .body(userResponse)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body.email").value("authenticated@example.com"));

        verify(userService).getAuthenticatedUser();
    }
}

