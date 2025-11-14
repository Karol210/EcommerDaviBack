package com.ecommerce.davivienda.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para AuthenticatedUserUtil.
 * Cubre extracción de datos del contexto de seguridad.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticatedUserUtil - Tests Unitarios")
class AuthenticatedUserUtilTest {

    @InjectMocks
    private AuthenticatedUserUtil authenticatedUserUtil;

    private SecurityContext mockSecurityContext;

    @BeforeEach
    void setUp() {
        mockSecurityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(mockSecurityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getCurrentUsername - Obtener username exitosamente")
    void testGetCurrentUsername_Success() {
        // Arrange
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("Cliente")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", "password", authorities
        );

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

        // Act
        String username = authenticatedUserUtil.getCurrentUsername();

        // Assert
        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("getCurrentUsername - Fallar cuando no hay autenticación")
    void testGetCurrentUsername_NoAuthentication_ThrowsException() {
        // Arrange
        when(mockSecurityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> authenticatedUserUtil.getCurrentUsername())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No hay usuario autenticado");
    }

    @Test
    @DisplayName("getCurrentUsername - Fallar cuando usuario es anónimo")
    void testGetCurrentUsername_AnonymousUser_ThrowsException() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "anonymousUser", null, Collections.emptyList()
        );

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

        // Act & Assert
        assertThatThrownBy(() -> authenticatedUserUtil.getCurrentUsername())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Usuario anónimo no tiene acceso");
    }

    @Test
    @DisplayName("getCurrentAuthorities - Obtener authorities exitosamente")
    void testGetCurrentAuthorities_Success() {
        // Arrange
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("Cliente"),
                new SimpleGrantedAuthority("ROLE_USER")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", "password", authorities
        );

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

        // Act
        Collection<? extends GrantedAuthority> result = authenticatedUserUtil.getCurrentAuthorities();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("Cliente", "ROLE_USER");
    }

    @Test
    @DisplayName("hasRole - Verificar rol exitosamente cuando el usuario tiene el rol")
    void testHasRole_UserHasRole_ReturnsTrue() {
        // Arrange
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("Cliente"),
                new SimpleGrantedAuthority("ROLE_USER")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", "password", authorities
        );

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

        // Act
        boolean hasRole = authenticatedUserUtil.hasRole("Cliente");

        // Assert
        assertThat(hasRole).isTrue();
    }

    @Test
    @DisplayName("hasRole - Verificar rol cuando el usuario NO tiene el rol")
    void testHasRole_UserDoesNotHaveRole_ReturnsFalse() {
        // Arrange
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("Cliente")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", "password", authorities
        );

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

        // Act
        boolean hasRole = authenticatedUserUtil.hasRole("Administrador");

        // Assert
        assertThat(hasRole).isFalse();
    }

    @Test
    @DisplayName("hasRole - Retornar false cuando no hay usuario autenticado")
    void testHasRole_NoAuthentication_ReturnsFalse() {
        // Arrange
        when(mockSecurityContext.getAuthentication()).thenReturn(null);

        // Act
        boolean hasRole = authenticatedUserUtil.hasRole("Cliente");

        // Assert
        assertThat(hasRole).isFalse();
    }

    @Test
    @DisplayName("isAuthenticated - Usuario autenticado correctamente")
    void testIsAuthenticated_AuthenticatedUser_ReturnsTrue() {
        // Arrange
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("Cliente")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", "password", authorities
        );

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

        // Act
        boolean isAuthenticated = authenticatedUserUtil.isAuthenticated();

        // Assert
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    @DisplayName("isAuthenticated - No hay autenticación")
    void testIsAuthenticated_NoAuthentication_ReturnsFalse() {
        // Arrange
        when(mockSecurityContext.getAuthentication()).thenReturn(null);

        // Act
        boolean isAuthenticated = authenticatedUserUtil.isAuthenticated();

        // Assert
        assertThat(isAuthenticated).isFalse();
    }

    @Test
    @DisplayName("isAuthenticated - Usuario anónimo")
    void testIsAuthenticated_AnonymousUser_ReturnsFalse() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "anonymousUser", null, Collections.emptyList()
        );

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

        // Act
        boolean isAuthenticated = authenticatedUserUtil.isAuthenticated();

        // Assert
        assertThat(isAuthenticated).isFalse();
    }
}

