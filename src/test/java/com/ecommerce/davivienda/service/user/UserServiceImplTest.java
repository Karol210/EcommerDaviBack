package com.ecommerce.davivienda.service.user;

import com.ecommerce.davivienda.entity.user.*;
import com.ecommerce.davivienda.exception.user.UserException;
import com.ecommerce.davivienda.mapper.user.UserMapper;
import com.ecommerce.davivienda.models.Response;
import com.ecommerce.davivienda.models.user.UserRequest;
import com.ecommerce.davivienda.models.user.UserResponse;
import com.ecommerce.davivienda.models.user.UserUpdateRequest;
import com.ecommerce.davivienda.service.auth.AuthUserService;
import com.ecommerce.davivienda.service.user.transactional.role.UserRoleTransactionalService;
import com.ecommerce.davivienda.service.user.transactional.user.UserUserTransactionalService;
import com.ecommerce.davivienda.service.user.validation.common.UserCommonValidationService;
import com.ecommerce.davivienda.service.user.validation.document.UserDocumentValidationService;
import com.ecommerce.davivienda.service.user.validation.role.UserRoleValidationService;
import com.ecommerce.davivienda.service.user.validation.status.UserStatusValidationService;
import com.ecommerce.davivienda.service.user.validation.user.UserUserValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.davivienda.constants.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UserServiceImpl.
 * Cubre todos los métodos y casos de borde del servicio coordinador de usuarios.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl - Tests Unitarios")
class UserServiceImplTest {

    @Mock
    private UserUserValidationService userValidationService;

    @Mock
    private UserDocumentValidationService documentValidationService;

    @Mock
    private UserRoleValidationService roleValidationService;

    @Mock
    private UserStatusValidationService statusValidationService;

    @Mock
    private UserCommonValidationService commonValidationService;

    @Mock
    private UserUserTransactionalService userTransactionalService;

    @Mock
    private UserRoleTransactionalService roleTransactionalService;

    @Mock
    private AuthUserService authUserService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private UserRequest mockUserRequest;
    private DocumentType mockDocumentType;
    private UserStatus mockUserStatus;
    private Role mockRole;
    private UserRole mockUserRole;

    @BeforeEach
    void setUp() {
        // Mock DocumentType
        mockDocumentType = new DocumentType();
        mockDocumentType.setDocumentoId(1);
        mockDocumentType.setCodigo("CC");
        mockDocumentType.setNombre("Cédula de Ciudadanía");

        // Mock UserStatus
        mockUserStatus = new UserStatus();
        mockUserStatus.setEstadoUsuarioId(1);
        mockUserStatus.setNombre("Activo");

        // Mock Role
        mockRole = new Role();
        mockRole.setRolId(1);
        mockRole.setNombreRol("Cliente");

        // Mock UserRole
        mockUserRole = new UserRole();
        mockUserRole.setUsuarioRolId(1);
        mockUserRole.setUsuarioId(1);
        mockUserRole.setRole(mockRole);

        // Mock User
        mockUser = new User();
        mockUser.setUsuarioId(1);
        Credentials credentials = Credentials.builder()
                .credencialesId(1)
                .correo("test@example.com")
                .contrasena("hashedPassword")
                .build();
        mockUser.setCredenciales(credentials);
        mockUser.setNombre("John");
        mockUser.setApellido("Doe");
        mockUser.setNumeroDeDoc("123456789");
        mockUser.setDocumentType(mockDocumentType);
        mockUser.setUserStatus(mockUserStatus);

        mockUser.setRoles(Collections.singletonList(mockUserRole));

        // Mock UserRequest
        mockUserRequest = UserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nombre("John")
                .apellido("Doe")
                .documentType("CC")
                .documentNumber("123456789")
                .roles(Collections.singletonList("Cliente"))
                .build();
    }

    @Test
    @DisplayName("createUser - Crear usuario exitosamente con nombre de tipo de documento")
    void testCreateUser_WithDocumentTypeName_Success() {
        // Arrange
        when(documentValidationService.validateDocumentTypeByName("CC")).thenReturn(mockDocumentType);
        when(roleValidationService.validateAndFindRolesByNames(anyList())).thenReturn(Collections.singletonList(mockRole));
        when(statusValidationService.findUserStatusByName("Activo")).thenReturn(mockUserStatus);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userMapper.toEntity(any(), any(), any(), anyString())).thenReturn(mockUser);
        when(userTransactionalService.saveUser(any(User.class))).thenReturn(mockUser);
        when(userMapper.buildUserRoles(anyInt(), anyList())).thenReturn(Collections.singletonList(mockUserRole));
        when(roleTransactionalService.saveAllUserRoles(anyList())).thenReturn(Collections.singletonList(mockUserRole));
        when(userMapper.assignPrimaryRole(any(User.class), anyList())).thenReturn(true);

        // Act
        Response<String> response = userService.createUser(mockUserRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo(SUCCESS_USER_CREATED);

        verify(userValidationService).validateEmailNotExists("test@example.com");
        verify(commonValidationService).validatePasswordNotEmpty("password123");
        verify(documentValidationService).validateDocumentTypeByName("CC");
        verify(roleValidationService).validateAndFindRolesByNames(anyList());
        verify(passwordEncoder).encode("password123");
        verify(userTransactionalService, times(2)).saveUser(any(User.class));
    }

    @Test
    @DisplayName("createUser - Crear usuario con ID de tipo de documento (formato antiguo)")
    void testCreateUser_WithDocumentTypeId_Success() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nombre("John")
                .apellido("Doe")
                .email("test@example.com")
                .documentTypeId(1)
                .documentNumber("123456789")
                .roleIds(Collections.singletonList(1))
                .build();

        when(documentValidationService.validateDocumentType(1)).thenReturn(mockDocumentType);
        when(roleValidationService.validateAndFindRolesByIds(anyList())).thenReturn(Collections.singletonList(mockRole));
        when(statusValidationService.findUserStatusByName("Activo")).thenReturn(mockUserStatus);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userMapper.toEntity(any(), any(), any(), anyString())).thenReturn(mockUser);
        when(userTransactionalService.saveUser(any(User.class))).thenReturn(mockUser);
        when(userMapper.buildUserRoles(anyInt(), anyList())).thenReturn(Collections.singletonList(mockUserRole));
        when(roleTransactionalService.saveAllUserRoles(anyList())).thenReturn(Collections.singletonList(mockUserRole));
        when(userMapper.assignPrimaryRole(any(User.class), anyList())).thenReturn(false);

        // Act
        Response<String> response = userService.createUser(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getMessage()).isEqualTo(SUCCESS_USER_CREATED);

        verify(documentValidationService).validateDocumentType(1);
        verify(roleValidationService).validateAndFindRolesByIds(anyList());
        verify(userTransactionalService, times(1)).saveUser(any(User.class));
    }

    @Test
    @DisplayName("createUser - Fallar cuando el email ya existe")
    void testCreateUser_EmailExists_ThrowsException() {
        // Arrange
        doThrow(new UserException(ERROR_EMAIL_EXISTS, CODE_EMAIL_EXISTS))
                .when(userValidationService).validateEmailNotExists("test@example.com");

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(mockUserRequest))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_EMAIL_EXISTS);

        verify(userValidationService).validateEmailNotExists("test@example.com");
        verify(userTransactionalService, never()).saveUser(any(User.class));
    }

    @Test
    @DisplayName("createUser - Fallar cuando no se proporciona tipo de documento")
    void testCreateUser_NoDocumentType_ThrowsException() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nombre("John")
                .apellido("Doe")
                .email("test@example.com")
                .documentNumber("123456789")
                .roles(Collections.singletonList("Cliente"))
                .build();

        when(documentValidationService.validateDocumentTypeByName(null)).thenReturn(null);
        when(documentValidationService.validateDocumentType(null)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_DOCUMENT_TYPE_NOT_FOUND);
    }

    @Test
    @DisplayName("createUser - Fallar cuando no se proporcionan roles")
    void testCreateUser_NoRoles_ThrowsException() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nombre("John")
                .apellido("Doe")
                .email("test@example.com")
                .documentType("CC")
                .documentNumber("123456789")
                .build();

        when(documentValidationService.validateDocumentTypeByName("CC")).thenReturn(mockDocumentType);
        when(roleValidationService.validateAndFindRolesByNames(null)).thenReturn(Collections.emptyList());
        when(roleValidationService.validateAndFindRolesByIds(null)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_ROLES_EMPTY);
    }

    @Test
    @DisplayName("updateUser - Actualizar usuario con ID proporcionado exitosamente")
    void testUpdateUser_WithId_Success() {
        // Arrange
        Integer userId = 1;
        Integer authenticatedUserRoleId = 1;

        UserUpdateRequest request = UserUpdateRequest.builder()
                .id(userId)
                .nombre("Jane")
                .apellido("Updated")
                .email("test@example.com")
                .build();

        when(authUserService.getAuthenticatedUserRoleId()).thenReturn(authenticatedUserRoleId);
        when(userValidationService.findUserByIdOrThrow(userId)).thenReturn(mockUser);
        when(userTransactionalService.saveUser(any(User.class))).thenReturn(mockUser);

        // Act
        Response<String> response = userService.updateUser(userId, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getMessage()).isEqualTo(SUCCESS_USER_UPDATED);

        verify(authUserService).getAuthenticatedUserRoleId();
        verify(userValidationService).validateUserOwnership(userId, authenticatedUserRoleId);
        verify(userValidationService).findUserByIdOrThrow(userId);
        verify(userTransactionalService).saveUser(any(User.class));
    }

    @Test
    @DisplayName("updateUser - Actualizar usuario autenticado sin ID")
    void testUpdateUser_WithoutId_Success() {
        // Arrange
        Integer authenticatedUserRoleId = 1;

        UserUpdateRequest request = UserUpdateRequest.builder()
                .nombre("Jane")
                .apellido("Updated")
                .email("test@example.com")
                .build();

        when(authUserService.getAuthenticatedUserRoleId()).thenReturn(authenticatedUserRoleId);
        when(userValidationService.findUserByUserRoleId(authenticatedUserRoleId)).thenReturn(mockUser);
        when(userValidationService.findUserByIdOrThrow(1)).thenReturn(mockUser);
        when(userTransactionalService.saveUser(any(User.class))).thenReturn(mockUser);

        // Act
        Response<String> response = userService.updateUser(null, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getMessage()).isEqualTo(SUCCESS_USER_UPDATED);

        verify(authUserService).getAuthenticatedUserRoleId();
        verify(userValidationService).findUserByUserRoleId(authenticatedUserRoleId);
        verify(userValidationService, never()).validateUserOwnership(anyInt(), anyInt());
    }

    @Test
    @DisplayName("recoverPassword - Recuperar contraseña exitosamente con envío de correo")
    void testRecoverPassword_WithEmail_Success() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword123";
        Boolean envioCorreo = true;

        when(userValidationService.findUserByEmailOrThrow(email)).thenReturn(mockUser);
        when(passwordEncoder.encode(newPassword)).thenReturn("newHashedPassword");
        when(userTransactionalService.saveUser(any(User.class))).thenReturn(mockUser);

        // Act
        Response<String> response = userService.recoverPassword(email, newPassword, envioCorreo);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getMessage()).isEqualTo(SUCCESS_PASSWORD_RECOVERY_EMAIL_SENT);

        verify(commonValidationService).validatePasswordNotEmpty(newPassword);
        verify(userValidationService).findUserByEmailOrThrow(email);
        verify(passwordEncoder).encode(newPassword);
        verify(userTransactionalService).saveUser(any(User.class));
    }

    @Test
    @DisplayName("recoverPassword - Recuperar contraseña sin envío de correo")
    void testRecoverPassword_WithoutEmail_Success() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword123";
        Boolean envioCorreo = false;

        when(userValidationService.findUserByEmailOrThrow(email)).thenReturn(mockUser);
        when(passwordEncoder.encode(newPassword)).thenReturn("newHashedPassword");
        when(userTransactionalService.saveUser(any(User.class))).thenReturn(mockUser);

        // Act
        Response<String> response = userService.recoverPassword(email, newPassword, envioCorreo);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getMessage()).isEqualTo(SUCCESS_PASSWORD_CHANGED);

        verify(commonValidationService).validatePasswordNotEmpty(newPassword);
        verify(userValidationService).findUserByEmailOrThrow(email);
        verify(passwordEncoder).encode(newPassword);
        verify(userTransactionalService).saveUser(any(User.class));
    }

    @Test
    @DisplayName("changePasswordAuthenticated - Cambiar contraseña autenticado exitosamente")
    void testChangePasswordAuthenticated_Success() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "newPassword123";
        Integer authenticatedUserRoleId = 1;

        when(authUserService.getAuthenticatedUserRoleId()).thenReturn(authenticatedUserRoleId);
        when(userValidationService.findUserByEmailOrThrow(email)).thenReturn(mockUser);
        when(passwordEncoder.encode(newPassword)).thenReturn("newHashedPassword");
        when(userTransactionalService.saveUser(any(User.class))).thenReturn(mockUser);

        // Act
        Response<String> response = userService.changePasswordAuthenticated(email, newPassword);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getMessage()).isEqualTo(SUCCESS_PASSWORD_CHANGED);

        verify(authUserService).getAuthenticatedUserRoleId();
        verify(userValidationService).validateEmailOwnership(email, authenticatedUserRoleId);
        verify(commonValidationService).validatePasswordNotEmpty(newPassword);
        verify(userValidationService).findUserByEmailOrThrow(email);
        verify(passwordEncoder).encode(newPassword);
        verify(userTransactionalService).saveUser(any(User.class));
    }

    @Test
    @DisplayName("getAllUsers - Obtener todos los usuarios exitosamente")
    void testGetAllUsers_Success() {
        // Arrange
        List<User> users = Arrays.asList(mockUser, mockUser);
        List<UserResponse> userResponses = Arrays.asList(
                UserResponse.builder().email("test1@example.com").build(),
                UserResponse.builder().email("test2@example.com").build()
        );

        when(userTransactionalService.findAllUsers()).thenReturn(users);
        when(userMapper.toResponseList(users)).thenReturn(userResponses);

        // Act
        Response<List<UserResponse>> response = userService.getAllUsers();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo(SUCCESS_USERS_FOUND);
        assertThat(response.getBody()).hasSize(2);

        verify(userTransactionalService).findAllUsers();
        verify(userMapper).toResponseList(users);
    }

    @Test
    @DisplayName("getUserById - Obtener usuario por ID exitosamente")
    void testGetUserById_Success() {
        // Arrange
        Integer userId = 1;
        UserResponse userResponse = UserResponse.builder()
                .usuarioId(userId)
                .email("test@example.com")
                .build();

        when(userTransactionalService.findUserById(userId)).thenReturn(Optional.of(mockUser));
        when(userMapper.toResponse(mockUser)).thenReturn(userResponse);

        // Act
        Response<UserResponse> response = userService.getUserById(userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo(SUCCESS_USER_FOUND);
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");

        verify(userTransactionalService).findUserById(userId);
        verify(userMapper).toResponse(mockUser);
    }

    @Test
    @DisplayName("getUserById - Fallar cuando el usuario no existe")
    void testGetUserById_NotFound_ThrowsException() {
        // Arrange
        Integer userId = 999;

        when(userTransactionalService.findUserById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_USER_NOT_FOUND);

        verify(userTransactionalService).findUserById(userId);
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    @DisplayName("getAuthenticatedUser - Obtener usuario autenticado exitosamente")
    void testGetAuthenticatedUser_Success() {
        // Arrange
        Integer authenticatedUserRoleId = 1;
        UserResponse userResponse = UserResponse.builder()
                .usuarioId(1)
                .email("test@example.com")
                .build();

        when(authUserService.getAuthenticatedUserRoleId()).thenReturn(authenticatedUserRoleId);
        when(userTransactionalService.findUserByUserRoleId(authenticatedUserRoleId)).thenReturn(Optional.of(mockUser));
        when(userMapper.toResponse(mockUser)).thenReturn(userResponse);

        // Act
        Response<UserResponse> response = userService.getAuthenticatedUser();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isFalse();
        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getMessage()).isEqualTo(SUCCESS_USER_FOUND);
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");

        verify(authUserService).getAuthenticatedUserRoleId();
        verify(userTransactionalService).findUserByUserRoleId(authenticatedUserRoleId);
        verify(userMapper).toResponse(mockUser);
    }

    @Test
    @DisplayName("getAuthenticatedUser - Fallar cuando el usuario autenticado no existe")
    void testGetAuthenticatedUser_NotFound_ThrowsException() {
        // Arrange
        Integer authenticatedUserRoleId = 999;

        when(authUserService.getAuthenticatedUserRoleId()).thenReturn(authenticatedUserRoleId);
        when(userTransactionalService.findUserByUserRoleId(authenticatedUserRoleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getAuthenticatedUser())
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_USER_NOT_FOUND);

        verify(authUserService).getAuthenticatedUserRoleId();
        verify(userTransactionalService).findUserByUserRoleId(authenticatedUserRoleId);
        verify(userMapper, never()).toResponse(any(User.class));
    }
}

