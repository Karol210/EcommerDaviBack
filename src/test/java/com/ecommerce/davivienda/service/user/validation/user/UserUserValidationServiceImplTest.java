package com.ecommerce.davivienda.service.user.validation.user;

import com.ecommerce.davivienda.entity.user.Credentials;
import com.ecommerce.davivienda.entity.user.User;
import com.ecommerce.davivienda.exception.user.UserException;
import com.ecommerce.davivienda.service.user.transactional.user.UserUserTransactionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ecommerce.davivienda.constants.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UserUserValidationServiceImpl.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserUserValidationServiceImpl - Tests Unitarios")
class UserUserValidationServiceImplTest {

    @Mock
    private UserUserTransactionalService transactionalService;

    @InjectMocks
    private UserUserValidationServiceImpl validationService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsuarioId(1);
        Credentials credentials = Credentials.builder()
                .correo("test@example.com")
                .build();
        mockUser.setCredenciales(credentials);
        mockUser.setUsuarioRolId(1);
        mockUser.setNumeroDeDoc("123456789");
    }

    @Test
    @DisplayName("validateEmailNotExists - Email no existe, validación exitosa")
    void testValidateEmailNotExists_Success() {
        // Arrange
        when(transactionalService.existsByEmail("newemail@example.com")).thenReturn(false);

        // Act & Assert (no debería lanzar excepción)
        validationService.validateEmailNotExists("newemail@example.com");

        verify(transactionalService).existsByEmail("newemail@example.com");
    }

    @Test
    @DisplayName("validateEmailNotExists - Email ya existe, lanza excepción")
    void testValidateEmailNotExists_EmailExists_ThrowsException() {
        // Arrange
        when(transactionalService.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> validationService.validateEmailNotExists("existing@example.com"))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_EMAIL_EXISTS);

        verify(transactionalService).existsByEmail("existing@example.com");
    }

    @Test
    @DisplayName("findUserByIdOrThrow - Usuario encontrado exitosamente")
    void testFindUserByIdOrThrow_Success() {
        // Arrange
        when(transactionalService.findUserById(1)).thenReturn(Optional.of(mockUser));

        // Act
        User result = validationService.findUserByIdOrThrow(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsuarioId()).isEqualTo(1);
        assertThat(result.getCorreo()).isEqualTo("test@example.com");

        verify(transactionalService).findUserById(1);
    }

    @Test
    @DisplayName("findUserByIdOrThrow - Usuario no encontrado, lanza excepción")
    void testFindUserByIdOrThrow_NotFound_ThrowsException() {
        // Arrange
        when(transactionalService.findUserById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> validationService.findUserByIdOrThrow(999))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_USER_NOT_FOUND);

        verify(transactionalService).findUserById(999);
    }

    @Test
    @DisplayName("findUserByEmailOrThrow - Usuario encontrado exitosamente")
    void testFindUserByEmailOrThrow_Success() {
        // Arrange
        when(transactionalService.findUserByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        // Act
        User result = validationService.findUserByEmailOrThrow("test@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCorreo()).isEqualTo("test@example.com");

        verify(transactionalService).findUserByEmail("test@example.com");
    }

    @Test
    @DisplayName("findUserByEmailOrThrow - Usuario no encontrado, lanza excepción")
    void testFindUserByEmailOrThrow_NotFound_ThrowsException() {
        // Arrange
        when(transactionalService.findUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> validationService.findUserByEmailOrThrow("nonexistent@example.com"))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_USER_NOT_FOUND);

        verify(transactionalService).findUserByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("findUserByUserRoleId - Usuario encontrado exitosamente")
    void testFindUserByUserRoleId_Success() {
        // Arrange
        when(transactionalService.findUserByUserRoleId(1)).thenReturn(Optional.of(mockUser));

        // Act
        User result = validationService.findUserByUserRoleId(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsuarioRolId()).isEqualTo(1);

        verify(transactionalService).findUserByUserRoleId(1);
    }

    @Test
    @DisplayName("validateDocumentCombination - Documento no existe, validación exitosa")
    void testValidateDocumentCombination_Success() {
        // Arrange
        when(transactionalService.findByDocumentTypeAndNumber(1, "123456789"))
                .thenReturn(Optional.empty());

        // Act & Assert (no debería lanzar excepción)
        validationService.validateDocumentCombination(1, "123456789", null);

        verify(transactionalService).findByDocumentTypeAndNumber(1, "123456789");
    }

    @Test
    @DisplayName("validateDocumentCombination - Documento existe en otro usuario, lanza excepción")
    void testValidateDocumentCombination_ExistsInOtherUser_ThrowsException() {
        // Arrange
        when(transactionalService.findByDocumentTypeAndNumber(1, "123456789"))
                .thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThatThrownBy(() -> validationService.validateDocumentCombination(1, "123456789", 999))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_DOCUMENT_COMBINATION_EXISTS);

        verify(transactionalService).findByDocumentTypeAndNumber(1, "123456789");
    }

    @Test
    @DisplayName("validateDocumentCombination - Documento existe en mismo usuario, validación exitosa")
    void testValidateDocumentCombination_ExistsInSameUser_Success() {
        // Arrange
        when(transactionalService.findByDocumentTypeAndNumber(1, "123456789"))
                .thenReturn(Optional.of(mockUser));

        // Act & Assert (no debería lanzar excepción)
        validationService.validateDocumentCombination(1, "123456789", 1);

        verify(transactionalService).findByDocumentTypeAndNumber(1, "123456789");
    }

    @Test
    @DisplayName("validateEmailUpdate - Email diferente y no existe, validación exitosa")
    void testValidateEmailUpdate_DifferentEmailNotExists_Success() {
        // Arrange
        when(transactionalService.existsByEmail("newemail@example.com")).thenReturn(false);

        // Act & Assert (no debería lanzar excepción)
        validationService.validateEmailUpdate("newemail@example.com", "oldemail@example.com");

        verify(transactionalService).existsByEmail("newemail@example.com");
    }

    @Test
    @DisplayName("validateEmailUpdate - Email igual al actual, no valida")
    void testValidateEmailUpdate_SameEmail_NoValidation() {
        // Act & Assert (no debería lanzar excepción ni llamar al servicio)
        validationService.validateEmailUpdate("test@example.com", "test@example.com");

        verify(transactionalService, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("validateEmailUpdate - Email null, no valida")
    void testValidateEmailUpdate_NullEmail_NoValidation() {
        // Act & Assert (no debería lanzar excepción ni llamar al servicio)
        validationService.validateEmailUpdate(null, "test@example.com");

        verify(transactionalService, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("validateUserOwnership - Usuario es dueño, validación exitosa")
    void testValidateUserOwnership_IsOwner_Success() {
        // Arrange
        when(transactionalService.findUserById(1)).thenReturn(Optional.of(mockUser));

        // Act & Assert (no debería lanzar excepción)
        validationService.validateUserOwnership(1, 1);

        verify(transactionalService).findUserById(1);
    }

    @Test
    @DisplayName("validateUserOwnership - Usuario no es dueño, lanza excepción")
    void testValidateUserOwnership_NotOwner_ThrowsException() {
        // Arrange
        when(transactionalService.findUserById(1)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThatThrownBy(() -> validationService.validateUserOwnership(1, 999))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_USER_UNAUTHORIZED_UPDATE);

        verify(transactionalService).findUserById(1);
    }

    @Test
    @DisplayName("validateEmailOwnership - Usuario es dueño del email, validación exitosa")
    void testValidateEmailOwnership_IsOwner_Success() {
        // Arrange
        when(transactionalService.findUserByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        // Act & Assert (no debería lanzar excepción)
        validationService.validateEmailOwnership("test@example.com", 1);

        verify(transactionalService).findUserByEmail("test@example.com");
    }

    @Test
    @DisplayName("validateEmailOwnership - Usuario no es dueño del email, lanza excepción")
    void testValidateEmailOwnership_NotOwner_ThrowsException() {
        // Arrange
        when(transactionalService.findUserByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThatThrownBy(() -> validationService.validateEmailOwnership("test@example.com", 999))
                .isInstanceOf(UserException.class)
                .hasMessageContaining(ERROR_USER_UNAUTHORIZED_PASSWORD_CHANGE);

        verify(transactionalService).findUserByEmail("test@example.com");
    }
}

