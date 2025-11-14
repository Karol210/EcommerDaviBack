package com.ecommerce.davivienda.service.cartitem.validation.common;

import com.ecommerce.davivienda.exception.cart.CartException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartItemCommonValidationServiceImpl - Tests Unitarios")
class CartItemCommonValidationServiceImplTest {

    @InjectMocks
    private CartItemCommonValidationServiceImpl validationService;

    @Test
    @DisplayName("validateQuantity - Cantidad válida, no lanza excepción")
    void testValidateQuantity_Valid_Success() {
        validationService.validateQuantity(1);
        validationService.validateQuantity(10);
        validationService.validateQuantity(100);
    }

    @Test
    @DisplayName("validateQuantity - Cantidad null, lanza excepción")
    void testValidateQuantity_Null_ThrowsException() {
        assertThatThrownBy(() -> validationService.validateQuantity(null))
                .isInstanceOf(CartException.class);
    }

    @Test
    @DisplayName("validateQuantity - Cantidad cero, lanza excepción")
    void testValidateQuantity_Zero_ThrowsException() {
        assertThatThrownBy(() -> validationService.validateQuantity(0))
                .isInstanceOf(CartException.class);
    }

    @Test
    @DisplayName("validateQuantity - Cantidad negativa, lanza excepción")
    void testValidateQuantity_Negative_ThrowsException() {
        assertThatThrownBy(() -> validationService.validateQuantity(-1))
                .isInstanceOf(CartException.class);
    }
}

