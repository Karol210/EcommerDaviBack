package com.ecommerce.davivienda.service.product.validation.product;

import com.ecommerce.davivienda.entity.product.Product;
import com.ecommerce.davivienda.exception.product.ProductException;
import com.ecommerce.davivienda.service.product.transactional.product.ProductProductTransactionalService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductProductValidationServiceImpl - Tests Unitarios")
class ProductProductValidationServiceImplTest {

    @Mock
    private ProductProductTransactionalService transactionalService;

    @InjectMocks
    private ProductProductValidationServiceImpl validationService;

    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setProductoId(1);
        mockProduct.setNombre("Laptop");
    }

    @Test
    @DisplayName("validateProductNameNotExists - Nombre no existe, no lanza excepción")
    void testValidateProductNameNotExists_NotExists_Success() {
        when(transactionalService.existsByName("Laptop")).thenReturn(false);

        validationService.validateProductNameNotExists("Laptop");

        verify(transactionalService).existsByName("Laptop");
    }

    @Test
    @DisplayName("validateProductNameNotExists - Nombre existe, lanza excepción")
    void testValidateProductNameNotExists_Exists_ThrowsException() {
        when(transactionalService.existsByName("Laptop")).thenReturn(true);

        assertThatThrownBy(() -> validationService.validateProductNameNotExists("Laptop"))
                .isInstanceOf(ProductException.class);

        verify(transactionalService).existsByName("Laptop");
    }

    @Test
    @DisplayName("validateProductNameNotExistsOnUpdate - Nombre no existe en otro producto")
    void testValidateProductNameNotExistsOnUpdate_NotExists_Success() {
        when(transactionalService.existsByNameAndNotId("Laptop", 1)).thenReturn(false);

        validationService.validateProductNameNotExistsOnUpdate("Laptop", 1);

        verify(transactionalService).existsByNameAndNotId("Laptop", 1);
    }

    @Test
    @DisplayName("validateProductNameNotExistsOnUpdate - Nombre existe en otro producto, lanza excepción")
    void testValidateProductNameNotExistsOnUpdate_Exists_ThrowsException() {
        when(transactionalService.existsByNameAndNotId("Laptop", 1)).thenReturn(true);

        assertThatThrownBy(() -> validationService.validateProductNameNotExistsOnUpdate("Laptop", 1))
                .isInstanceOf(ProductException.class);

        verify(transactionalService).existsByNameAndNotId("Laptop", 1);
    }

    @Test
    @DisplayName("findProductByIdOrThrow - Producto existe, retorna producto")
    void testFindProductByIdOrThrow_Exists_Success() {
        when(transactionalService.findProductById(1)).thenReturn(Optional.of(mockProduct));

        Product result = validationService.findProductByIdOrThrow(1);

        assertThat(result).isNotNull();
        assertThat(result.getProductoId()).isEqualTo(1);
        verify(transactionalService).findProductById(1);
    }

    @Test
    @DisplayName("findProductByIdOrThrow - Producto no encontrado, lanza excepción")
    void testFindProductByIdOrThrow_NotFound_ThrowsException() {
        when(transactionalService.findProductById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.findProductByIdOrThrow(999))
                .isInstanceOf(ProductException.class);

        verify(transactionalService).findProductById(999);
    }
}

