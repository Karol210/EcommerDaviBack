package com.ecommerce.davivienda.exception;

import com.ecommerce.davivienda.exception.cart.CartException;
import com.ecommerce.davivienda.exception.document.DocumentTypeException;
import com.ecommerce.davivienda.exception.payment.PaymentException;
import com.ecommerce.davivienda.exception.product.ProductException;
import com.ecommerce.davivienda.exception.role.RoleException;
import com.ecommerce.davivienda.exception.stock.InsufficientStockException;
import com.ecommerce.davivienda.exception.stock.StockException;
import com.ecommerce.davivienda.exception.user.UserException;
import com.ecommerce.davivienda.models.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Collections;

import static com.ecommerce.davivienda.constants.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para ExceptionHandlerController.
 * Cubre manejo de todas las excepciones del sistema.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExceptionHandlerController - Tests Unitarios")
class ExceptionHandlerControllerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ExceptionHandlerController exceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    @DisplayName("handleUserException - Usuario no encontrado retorna 404")
    void testHandleUserException_NotFound_Returns404() {
        // Arrange
        UserException exception = new UserException(ERROR_USER_NOT_FOUND, CODE_USER_NOT_FOUND);

        // Act
        Response<Object> response = exceptionHandler.handleUserException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getErrorCode()).isEqualTo(CODE_USER_NOT_FOUND);
        assertThat(response.getMessage()).isEqualTo(ERROR_USER_NOT_FOUND);
    }

    @Test
    @DisplayName("handleUserException - Otros errores retornan 400")
    void testHandleUserException_OtherError_Returns400() {
        // Arrange
        UserException exception = new UserException(ERROR_EMAIL_EXISTS, CODE_EMAIL_EXISTS);

        // Act
        Response<Object> response = exceptionHandler.handleUserException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorCode()).isEqualTo(CODE_EMAIL_EXISTS);
    }

    @Test
    @DisplayName("handleProductException - Producto no encontrado retorna 404")
    void testHandleProductException_NotFound_Returns404() {
        // Arrange
        ProductException exception = new ProductException(ERROR_PRODUCT_NOT_FOUND, CODE_PRODUCT_NOT_FOUND);

        // Act
        Response<Object> response = exceptionHandler.handleProductException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getErrorCode()).isEqualTo(CODE_PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("handleCartException - Carrito no encontrado retorna 404")
    void testHandleCartException_NotFound_Returns404() {
        // Arrange
        CartException exception = new CartException(ERROR_CART_NOT_FOUND, CODE_CART_NOT_FOUND);

        // Act
        Response<Object> response = exceptionHandler.handleCartException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("handleCartException - Autenticación requerida retorna 401")
    void testHandleCartException_AuthRequired_Returns401() {
        // Arrange
        CartException exception = new CartException(
                ERROR_CART_AUTHENTICATION_REQUIRED, 
                CODE_CART_AUTHENTICATION_REQUIRED
        );

        // Act
        Response<Object> response = exceptionHandler.handleCartException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("handleCartException - Usuario sin rol cliente retorna 403")
    void testHandleCartException_NotClientRole_Returns403() {
        // Arrange
        CartException exception = new CartException(
                ERROR_USER_NOT_CLIENT_ROLE, 
                CODE_USER_NOT_CLIENT_ROLE
        );

        // Act
        Response<Object> response = exceptionHandler.handleCartException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("handleDocumentTypeException - Retorna 400")
    void testHandleDocumentTypeException_Returns400() {
        // Arrange
        DocumentTypeException exception = new DocumentTypeException(
                ERROR_DOCUMENT_TYPE_NOT_FOUND, 
                CODE_DOCUMENT_TYPE_NOT_FOUND
        );

        // Act
        Response<Object> response = exceptionHandler.handleDocumentTypeException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("handleRoleException - Retorna 400")
    void testHandleRoleException_Returns400() {
        // Arrange
        RoleException exception = new RoleException(ERROR_ROLE_NOT_FOUND, CODE_ROLE_NOT_FOUND);

        // Act
        Response<Object> response = exceptionHandler.handleRoleException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("handleInsufficientStockException - Retorna 400 con detalles")
    void testHandleInsufficientStockException_Returns400WithDetails() {
        // Arrange
        InsufficientStockException exception = new InsufficientStockException(
                "Stock insuficiente",
                CODE_INSUFFICIENT_STOCK,
                Collections.emptyList()
        );

        // Act
        Response response = exceptionHandler.handleInsufficientStockException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    // TODO: Descomentar cuando se creen las constantes CODE_STOCK_VALIDATION_ERROR en Constants.java
    // @Test
    // @DisplayName("handleStockException - Retorna 400")
    // void testHandleStockException_Returns400() {
    //     // Arrange
    //     StockException exception = new StockException("Error de stock", CODE_STOCK_VALIDATION_ERROR);
    //
    //     // Act
    //     Response<Object> response = exceptionHandler.handleStockException(exception, request);
    //
    //     // Assert
    //     assertThat(response).isNotNull();
    //     assertThat(response.getFailure()).isTrue();
    //     assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    // }

    @Test
    @DisplayName("handleValidationException - Retorna 400 con errores de validación")
    void testHandleValidationException_Returns400WithErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("object", "email", "must not be empty");
        FieldError fieldError2 = new FieldError("object", "password", "must be at least 8 characters");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // Act
        Response<Object> response = exceptionHandler.handleValidationException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).contains("email");
        assertThat(response.getMessage()).contains("password");
    }

    @Test
    @DisplayName("handleDataIntegrityViolation - Retorna 409")
    void testHandleDataIntegrityViolation_Returns409() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Duplicado");

        // Act
        Response<Object> response = exceptionHandler.handleDataIntegrityViolation(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getErrorCode()).isEqualTo(CODE_DATA_INTEGRITY_VIOLATION);
    }

    @Test
    @DisplayName("handleAccessDeniedException - Retorna 403")
    void testHandleAccessDeniedException_Returns403() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Acceso denegado");

        // Act
        Response<Object> response = exceptionHandler.handleAccessDeniedException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getErrorCode()).isEqualTo(CODE_ACCESS_DENIED);
    }

    // TODO: Descomentar cuando se creen las constantes CODE_PAYMENT_ERROR en Constants.java
    // @Test
    // @DisplayName("handlePaymentException - Retorna 400")
    // void testHandlePaymentException_Returns400() {
    //     // Arrange
    //     PaymentException exception = new PaymentException("Error de pago", CODE_PAYMENT_ERROR);
    //
    //     // Act
    //     Response<Object> response = exceptionHandler.handlePaymentException(exception, request);
    //
    //     // Assert
    //     assertThat(response).isNotNull();
    //     assertThat(response.getFailure()).isTrue();
    //     assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    //     assertThat(response.getMessage()).contains(CODE_PAYMENT_ERROR);
    // }

    @Test
    @DisplayName("handleGenericException - Retorna 500")
    void testHandleGenericException_Returns500() {
        // Arrange
        Exception exception = new RuntimeException("Error inesperado");

        // Act
        Response<Object> response = exceptionHandler.handleGenericException(exception, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFailure()).isTrue();
        assertThat(response.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getErrorCode()).isEqualTo(CODE_GENERIC_ERROR);
    }
}

