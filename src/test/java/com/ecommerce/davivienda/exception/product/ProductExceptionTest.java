package com.ecommerce.davivienda.exception.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.ecommerce.davivienda.constants.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para ProductException.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@DisplayName("ProductException - Tests Unitarios")
class ProductExceptionTest {

    @Test
    @DisplayName("Constructor con mensaje y código - Crear excepción correctamente")
    void testConstructorWithMessageAndCode() {
        // Arrange & Act
        ProductException exception = new ProductException(ERROR_PRODUCT_NOT_FOUND, CODE_PRODUCT_NOT_FOUND);

        // Assert
        assertThat(exception.getMessage()).isEqualTo(ERROR_PRODUCT_NOT_FOUND);
        assertThat(exception.getErrorCode()).isEqualTo(CODE_PRODUCT_NOT_FOUND);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Constructor con mensaje, código y causa - Crear excepción correctamente")
    void testConstructorWithMessageCodeAndCause() {
        // Arrange
        Throwable cause = new RuntimeException("Causa raíz");

        // Act
        ProductException exception = new ProductException(ERROR_PRODUCT_NOT_FOUND, CODE_PRODUCT_NOT_FOUND, cause);

        // Assert
        assertThat(exception.getMessage()).isEqualTo(ERROR_PRODUCT_NOT_FOUND);
        assertThat(exception.getErrorCode()).isEqualTo(CODE_PRODUCT_NOT_FOUND);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("ProductException es RuntimeException - Verificar herencia")
    void testIsRuntimeException() {
        // Arrange & Act
        ProductException exception = new ProductException(ERROR_PRODUCT_NOT_FOUND, CODE_PRODUCT_NOT_FOUND);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}

