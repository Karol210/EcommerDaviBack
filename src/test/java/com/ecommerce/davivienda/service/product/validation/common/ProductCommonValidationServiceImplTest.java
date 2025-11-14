package com.ecommerce.davivienda.service.product.validation.common;

import com.ecommerce.davivienda.exception.product.ProductException;
import com.ecommerce.davivienda.models.product.ProductRequest;
import com.ecommerce.davivienda.models.product.ProductUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCommonValidationServiceImpl - Tests Unitarios")
class ProductCommonValidationServiceImplTest {

    @InjectMocks
    private ProductCommonValidationServiceImpl validationService;

    @Test
    @DisplayName("validatePrices (ProductRequest) - IVA válido, no lanza excepción")
    void testValidatePrices_Request_ValidIva_Success() {
        ProductRequest request = ProductRequest.builder()
                .name("Test")
                .unitValue(new BigDecimal("100"))
                .iva(new BigDecimal("19"))
                .build();

        validationService.validatePrices(request);
    }

    @Test
    @DisplayName("validatePrices (ProductRequest) - IVA null, no lanza excepción")
    void testValidatePrices_Request_NullIva_Success() {
        ProductRequest request = ProductRequest.builder()
                .name("Test")
                .unitValue(new BigDecimal("100"))
                .build();

        validationService.validatePrices(request);
    }

    @Test
    @DisplayName("validatePrices (ProductRequest) - IVA negativo, lanza excepción")
    void testValidatePrices_Request_NegativeIva_ThrowsException() {
        ProductRequest request = ProductRequest.builder()
                .name("Test")
                .unitValue(new BigDecimal("100"))
                .iva(new BigDecimal("-5"))
                .build();

        assertThatThrownBy(() -> validationService.validatePrices(request))
                .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("validatePrices (ProductRequest) - IVA mayor a 100, lanza excepción")
    void testValidatePrices_Request_IvaOver100_ThrowsException() {
        ProductRequest request = ProductRequest.builder()
                .name("Test")
                .unitValue(new BigDecimal("100"))
                .iva(new BigDecimal("101"))
                .build();

        assertThatThrownBy(() -> validationService.validatePrices(request))
                .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("validatePrices (ProductUpdateRequest) - IVA válido, no lanza excepción")
    void testValidatePrices_UpdateRequest_ValidIva_Success() {
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .iva(new BigDecimal("19"))
                .build();

        validationService.validatePrices(request);
    }

    @Test
    @DisplayName("validatePrices (ProductUpdateRequest) - IVA negativo, lanza excepción")
    void testValidatePrices_UpdateRequest_NegativeIva_ThrowsException() {
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .iva(new BigDecimal("-5"))
                .build();

        assertThatThrownBy(() -> validationService.validatePrices(request))
                .isInstanceOf(ProductException.class);
    }
}

