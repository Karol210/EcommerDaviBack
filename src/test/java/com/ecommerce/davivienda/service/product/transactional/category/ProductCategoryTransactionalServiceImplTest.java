package com.ecommerce.davivienda.service.product.transactional.category;

import com.ecommerce.davivienda.entity.product.Category;
import com.ecommerce.davivienda.repository.product.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCategoryTransactionalServiceImpl - Tests Unitarios")
class ProductCategoryTransactionalServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductCategoryTransactionalServiceImpl transactionalService;

    private Category mockCategory;

    @BeforeEach
    void setUp() {
        mockCategory = new Category();
        mockCategory.setCategoriaId(1);
        mockCategory.setNombre("Electrónicos");
    }

    @Test
    @DisplayName("findCategoryByName - Categoría existe")
    void testFindCategoryByName_Exists() {
        when(categoryRepository.findByNombreIgnoreCase("Electrónicos")).thenReturn(Optional.of(mockCategory));

        Optional<Category> result = transactionalService.findCategoryByName("Electrónicos");

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("Electrónicos");
        verify(categoryRepository).findByNombreIgnoreCase("Electrónicos");
    }

    @Test
    @DisplayName("findCategoryByName - Categoría no existe")
    void testFindCategoryByName_NotExists() {
        when(categoryRepository.findByNombreIgnoreCase("NoExiste")).thenReturn(Optional.empty());

        Optional<Category> result = transactionalService.findCategoryByName("NoExiste");

        assertThat(result).isEmpty();
        verify(categoryRepository).findByNombreIgnoreCase("NoExiste");
    }

    @Test
    @DisplayName("findCategoryByName - Búsqueda case insensitive")
    void testFindCategoryByName_CaseInsensitive() {
        when(categoryRepository.findByNombreIgnoreCase("electrónicos")).thenReturn(Optional.of(mockCategory));

        Optional<Category> result = transactionalService.findCategoryByName("electrónicos");

        assertThat(result).isPresent();
        verify(categoryRepository).findByNombreIgnoreCase("electrónicos");
    }
}

