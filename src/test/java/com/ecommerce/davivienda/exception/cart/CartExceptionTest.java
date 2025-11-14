package com.ecommerce.davivienda.exception.cart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.ecommerce.davivienda.constants.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para CartException.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@DisplayName("CartException - Tests Unitarios")
class CartExceptionTest {

    @Test
    @DisplayName("Constructor con mensaje y código - Crear excepción correctamente")
    void testConstructorWithMessageAndCode() {
        // Arrange & Act
        CartException exception = new CartException(ERROR_CART_NOT_FOUND, CODE_CART_NOT_FOUND);

        // Assert
        assertThat(exception.getMessage()).isEqualTo(ERROR_CART_NOT_FOUND);
        assertThat(exception.getErrorCode()).isEqualTo(CODE_CART_NOT_FOUND);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Constructor con mensaje, código y causa - Crear excepción correctamente")
    void testConstructorWithMessageCodeAndCause() {
        // Arrange
        Throwable cause = new RuntimeException("Causa raíz");

        // Act
        CartException exception = new CartException(ERROR_CART_NOT_FOUND, CODE_CART_NOT_FOUND, cause);

        // Assert
        assertThat(exception.getMessage()).isEqualTo(ERROR_CART_NOT_FOUND);
        assertThat(exception.getErrorCode()).isEqualTo(CODE_CART_NOT_FOUND);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("CartException es RuntimeException - Verificar herencia")
    void testIsRuntimeException() {
        // Arrange & Act
        CartException exception = new CartException(ERROR_CART_NOT_FOUND, CODE_CART_NOT_FOUND);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}

