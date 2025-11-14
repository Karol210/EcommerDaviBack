package com.ecommerce.davivienda.service.cartitem.validation.product;

import com.ecommerce.davivienda.entity.product.Product;
import com.ecommerce.davivienda.exception.product.ProductException;
import com.ecommerce.davivienda.service.cartitem.transactional.product.CartItemProductTransactionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartItemProductValidationServiceImpl - Tests Unitarios")
class CartItemProductValidationServiceImplTest {

    @Mock
    private CartItemProductTransactionalService transactionalService;

    @InjectMocks
    private CartItemProductValidationServiceImpl validationService;

    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setProductoId(1);
        mockProduct.setEstadoProductoId(1); // Activo
    }

    @Test
    @DisplayName("validateProductExists - Producto existe, retorna producto")
    void testValidateProductExists_Success() {
        when(transactionalService.findProductById(1)).thenReturn(Optional.of(mockProduct));

        Product result = validationService.validateProductExists(1);

        assertThat(result).isNotNull();
        assertThat(result.getProductoId()).isEqualTo(1);
    }

    @Test
    @DisplayName("validateProductExists - ProductId null, lanza excepción")
    void testValidateProductExists_NullId_ThrowsException() {
        assertThatThrownBy(() -> validationService.validateProductExists(null))
                .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("validateProductExists - Producto no encontrado, lanza excepción")
    void testValidateProductExists_NotFound_ThrowsException() {
        when(transactionalService.findProductById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.validateProductExists(999))
                .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("validateProductActive - Producto activo, no lanza excepción")
    void testValidateProductActive_Success() {
        validationService.validateProductActive(mockProduct);
    }

    @Test
    @DisplayName("validateProductActive - Producto inactivo, lanza excepción")
    void testValidateProductActive_Inactive_ThrowsException() {
        mockProduct.setEstadoProductoId(2); // Inactivo

        assertThatThrownBy(() -> validationService.validateProductActive(mockProduct))
                .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("validateProductActive - Producto null, lanza excepción")
    void testValidateProductActive_NullProduct_ThrowsException() {
        assertThatThrownBy(() -> validationService.validateProductActive(null))
                .isInstanceOf(ProductException.class);
    }
}

