package com.ecommerce.davivienda.service.auth;

import com.ecommerce.davivienda.entity.user.Credentials;
import com.ecommerce.davivienda.entity.user.Role;
import com.ecommerce.davivienda.entity.user.User;
import com.ecommerce.davivienda.entity.user.UserRole;
import com.ecommerce.davivienda.exception.cart.CartException;
import com.ecommerce.davivienda.repository.user.UserRepository;
import com.ecommerce.davivienda.repository.user.UserRoleRepository;
import com.ecommerce.davivienda.util.AuthenticatedUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static com.ecommerce.davivienda.constants.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthUserServiceImpl.
 * Cubre autenticación y validación de roles de usuario.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthUserServiceImpl - Tests Unitarios")
class AuthUserServiceImplTest {

    @Mock
    private AuthenticatedUserUtil authenticatedUserUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private AuthUserServiceImpl authUserService;

    private User mockUser;
    private UserRole mockUserRole;
    private Role mockRole;

    @BeforeEach
    void setUp() {
        // Mock Role
        mockRole = new Role();
        mockRole.setRolId(1);
        mockRole.setNombreRol("Cliente");
        mockRole.setNombreRol("Cliente");

        // Mock UserRole
        mockUserRole = new UserRole();
        mockUserRole.setUsuarioRolId(1);
        mockUserRole.setRole(mockRole);

        // Mock User
        mockUser = new User();
        mockUser.setUsuarioId(1);
        Credentials credentials = Credentials.builder()
                .correo("test@example.com")
                .build();
        mockUser.setCredenciales(credentials);
        mockUser.setRoles(Collections.singletonList(mockUserRole));
    }

    @Test
    @DisplayName("getAuthenticatedUserRoleId - Obtener userRoleId exitosamente")
    void testGetAuthenticatedUserRoleId_Success() {
        // Arrange
        String email = "test@example.com";

        when(authenticatedUserUtil.getCurrentUsername()).thenReturn(email);
        when(userRepository.findByCredenciales_Correo(email)).thenReturn(Optional.of(mockUser));
        when(userRoleRepository.findById(1)).thenReturn(Optional.of(mockUserRole));

        // Act
        Integer userRoleId = authUserService.getAuthenticatedUserRoleId();

        // Assert
        assertThat(userRoleId).isEqualTo(1);

        verify(authenticatedUserUtil).getCurrentUsername();
        verify(userRepository).findByCredenciales_Correo(email);
        verify(userRoleRepository).findById(1);
    }

    @Test
    @DisplayName("getAuthenticatedUserRoleId - Fallar cuando no hay usuario autenticado")
    void testGetAuthenticatedUserRoleId_NotAuthenticated_ThrowsException() {
        // Arrange
        when(authenticatedUserUtil.getCurrentUsername())
                .thenThrow(new IllegalStateException("No hay usuario autenticado"));

        // Act & Assert
        assertThatThrownBy(() -> authUserService.getAuthenticatedUserRoleId())
                .isInstanceOf(CartException.class)
                .hasMessageContaining(ERROR_CART_AUTHENTICATION_REQUIRED);

        verify(authenticatedUserUtil).getCurrentUsername();
        verify(userRepository, never()).findByCredenciales_Correo(anyString());
    }

    @Test
    @DisplayName("getAuthenticatedUserRoleId - Fallar cuando el usuario no existe")
    void testGetAuthenticatedUserRoleId_UserNotFound_ThrowsException() {
        // Arrange
        String email = "nonexistent@example.com";

        when(authenticatedUserUtil.getCurrentUsername()).thenReturn(email);
        when(userRepository.findByCredenciales_Correo(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authUserService.getAuthenticatedUserRoleId())
                .isInstanceOf(CartException.class)
                .hasMessageContaining(ERROR_USER_NOT_FOUND_BY_DOCUMENT);

        verify(authenticatedUserUtil).getCurrentUsername();
        verify(userRepository).findByCredenciales_Correo(email);
        verify(userRoleRepository, never()).findById(anyInt());
    }

    @Test
    @DisplayName("getAuthenticatedUserRoleId - Fallar cuando el usuario no tiene roles")
    void testGetAuthenticatedUserRoleId_UserWithoutRoles_ThrowsException() {
        // Arrange
        String email = "test@example.com";
        User userWithoutRoles = new User();
        userWithoutRoles.setUsuarioId(1);
        Credentials userWithoutRolesCredentials = Credentials.builder()
                .correo(email)
                .build();
        userWithoutRoles.setCredenciales(userWithoutRolesCredentials);
        userWithoutRoles.setRoles(Collections.emptyList());

        when(authenticatedUserUtil.getCurrentUsername()).thenReturn(email);
        when(userRepository.findByCredenciales_Correo(email)).thenReturn(Optional.of(userWithoutRoles));

        // Act & Assert
        assertThatThrownBy(() -> authUserService.getAuthenticatedUserRoleId())
                .isInstanceOf(CartException.class)
                .hasMessageContaining(ERROR_USER_WITHOUT_ROLES);

        verify(authenticatedUserUtil).getCurrentUsername();
        verify(userRepository).findByCredenciales_Correo(email);
        verify(userRoleRepository, never()).findById(anyInt());
    }

    @Test
    @DisplayName("getAuthenticatedUserRoleId - Fallar cuando el UserRole no existe")
    void testGetAuthenticatedUserRoleId_UserRoleNotFound_ThrowsException() {
        // Arrange
        String email = "test@example.com";

        when(authenticatedUserUtil.getCurrentUsername()).thenReturn(email);
        when(userRepository.findByCredenciales_Correo(email)).thenReturn(Optional.of(mockUser));
        when(userRoleRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authUserService.getAuthenticatedUserRoleId())
                .isInstanceOf(CartException.class)
                .hasMessageContaining(ERROR_USER_ROLE_NOT_FOUND);

        verify(authenticatedUserUtil).getCurrentUsername();
        verify(userRepository).findByCredenciales_Correo(email);
        verify(userRoleRepository).findById(1);
    }

    @Test
    @DisplayName("getAuthenticatedUserRoleId - Fallar cuando el usuario no tiene rol de Cliente")
    void testGetAuthenticatedUserRoleId_NotClientRole_ThrowsException() {
        // Arrange
        String email = "admin@example.com";

        Role adminRole = new Role();
        adminRole.setRolId(2);
        adminRole.setNombreRol("Administrador");
        adminRole.setNombreRol("Administrador");

        UserRole adminUserRole = new UserRole();
        adminUserRole.setUsuarioRolId(2);
        adminUserRole.setRole(adminRole);

        User adminUser = new User();
        adminUser.setUsuarioId(2);
        Credentials adminUserCredentials = Credentials.builder()
                .correo(email)
                .build();
        adminUser.setCredenciales(adminUserCredentials);
        adminUser.setRoles(Collections.singletonList(adminUserRole));

        when(authenticatedUserUtil.getCurrentUsername()).thenReturn(email);
        when(userRepository.findByCredenciales_Correo(email)).thenReturn(Optional.of(adminUser));
        when(userRoleRepository.findById(2)).thenReturn(Optional.of(adminUserRole));

        // Act & Assert
        assertThatThrownBy(() -> authUserService.getAuthenticatedUserRoleId())
                .isInstanceOf(CartException.class)
                .hasMessageContaining(ERROR_USER_NOT_CLIENT_ROLE);

        verify(authenticatedUserUtil).getCurrentUsername();
        verify(userRepository).findByCredenciales_Correo(email);
        verify(userRoleRepository).findById(2);
    }
}

