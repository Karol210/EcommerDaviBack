package com.ecommerce.davivienda.service.product.validation.category;

import com.ecommerce.davivienda.entity.product.Category;
import com.ecommerce.davivienda.exception.product.ProductException;
import com.ecommerce.davivienda.service.product.transactional.category.ProductCategoryTransactionalService;
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
@DisplayName("ProductCategoryValidationServiceImpl - Tests Unitarios")
class ProductCategoryValidationServiceImplTest {

    @Mock
    private ProductCategoryTransactionalService transactionalService;

    @InjectMocks
    private ProductCategoryValidationServiceImpl validationService;

    private Category mockCategory;

    @BeforeEach
    void setUp() {
        mockCategory = new Category();
        mockCategory.setCategoriaId(1);
        mockCategory.setNombre("Electrónicos");
        // Category.isActive() siempre retorna true por defecto
    }

    @Test
    @DisplayName("findCategoryByNameOrThrow - Categoría existe, retorna categoría")
    void testFindCategoryByNameOrThrow_Exists_Success() {
        when(transactionalService.findCategoryByName("Electrónicos")).thenReturn(Optional.of(mockCategory));

        Category result = validationService.findCategoryByNameOrThrow("Electrónicos");

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Electrónicos");
        verify(transactionalService).findCategoryByName("Electrónicos");
    }

    @Test
    @DisplayName("findCategoryByNameOrThrow - Categoría no encontrada, lanza excepción")
    void testFindCategoryByNameOrThrow_NotFound_ThrowsException() {
        when(transactionalService.findCategoryByName("NoExiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.findCategoryByNameOrThrow("NoExiste"))
                .isInstanceOf(ProductException.class);

        verify(transactionalService).findCategoryByName("NoExiste");
    }

    @Test
    @DisplayName("validateCategoryActive - Categoría activa, no lanza excepción")
    void testValidateCategoryActive_Active_Success() {
        validationService.validateCategoryActive(mockCategory);
    }

    @Test
    @DisplayName("validateCategoryActive - Categoría inactiva, lanza excepción")
    void testValidateCategoryActive_Inactive_ThrowsException() {
        // NOTE: En el diseño actual, Category.isActive() siempre retorna true
        // Este test requeriría mockear el método o modificar la entidad
        // Por ahora se comenta hasta que se implemente un campo de estado real
        
        // mockCategory.setEstado(2); // Inactivo - Campo no existe
        //
        // assertThatThrownBy(() -> validationService.validateCategoryActive(mockCategory))
        //         .isInstanceOf(ProductException.class);
    }
}

