package com.ecommerce.davivienda.exception.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.ecommerce.davivienda.constants.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para UserException.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@DisplayName("UserException - Tests Unitarios")
class UserExceptionTest {

    @Test
    @DisplayName("Constructor con mensaje y código - Crear excepción correctamente")
    void testConstructorWithMessageAndCode() {
        // Arrange & Act
        UserException exception = new UserException(ERROR_USER_NOT_FOUND, CODE_USER_NOT_FOUND);

        // Assert
        assertThat(exception.getMessage()).isEqualTo(ERROR_USER_NOT_FOUND);
        assertThat(exception.getErrorCode()).isEqualTo(CODE_USER_NOT_FOUND);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Constructor con mensaje, código y causa - Crear excepción correctamente")
    void testConstructorWithMessageCodeAndCause() {
        // Arrange
        Throwable cause = new RuntimeException("Causa raíz");

        // Act
        UserException exception = new UserException(ERROR_USER_NOT_FOUND, CODE_USER_NOT_FOUND, cause);

        // Assert
        assertThat(exception.getMessage()).isEqualTo(ERROR_USER_NOT_FOUND);
        assertThat(exception.getErrorCode()).isEqualTo(CODE_USER_NOT_FOUND);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Causa raíz");
    }

    @Test
    @DisplayName("getErrorCode - Obtener código de error correctamente")
    void testGetErrorCode() {
        // Arrange
        UserException exception = new UserException(ERROR_EMAIL_EXISTS, CODE_EMAIL_EXISTS);

        // Act
        String errorCode = exception.getErrorCode();

        // Assert
        assertThat(errorCode).isEqualTo(CODE_EMAIL_EXISTS);
    }

    @Test
    @DisplayName("UserException es RuntimeException - Verificar herencia")
    void testIsRuntimeException() {
        // Arrange & Act
        UserException exception = new UserException(ERROR_USER_NOT_FOUND, CODE_USER_NOT_FOUND);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}

