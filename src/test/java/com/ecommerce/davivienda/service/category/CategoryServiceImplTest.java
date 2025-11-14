package com.ecommerce.davivienda.service.category;

import com.ecommerce.davivienda.dto.category.CategoryResponseDto;
import com.ecommerce.davivienda.entity.product.Category;
import com.ecommerce.davivienda.exception.product.ProductException;
import com.ecommerce.davivienda.mapper.category.CategoryMapper;
import com.ecommerce.davivienda.repository.product.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceImpl - Tests Unitarios")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category mockCategory;
    private CategoryResponseDto mockResponseDto;

    @BeforeEach
    void setUp() {
        mockCategory = new Category();
        mockCategory.setCategoriaId(1);
        mockCategory.setNombre("Electrónicos");
        mockCategory.setDescripcion("Productos electrónicos");

        mockResponseDto = CategoryResponseDto.builder()
                .id(1)
                .name("Electrónicos")
                .description("Productos electrónicos")
                .build();
    }

    @Test
    @DisplayName("getAllCategories - Retorna lista de categorías")
    void testGetAllCategories_Success() {
        List<Category> categories = Arrays.asList(mockCategory);
        List<CategoryResponseDto> responseDtos = Arrays.asList(mockResponseDto);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponseDtoList(categories)).thenReturn(responseDtos);

        List<CategoryResponseDto> result = categoryService.getAllCategories();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Electrónicos");

        verify(categoryRepository).findAll();
        verify(categoryMapper).toResponseDtoList(categories);
    }

    @Test
    @DisplayName("getCategoryById - Encontrar categoría por ID")
    void testGetCategoryById_Success() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));
        when(categoryMapper.toResponseDto(mockCategory)).thenReturn(mockResponseDto);

        CategoryResponseDto result = categoryService.getCategoryById(1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electrónicos");

        verify(categoryRepository).findById(1);
        verify(categoryMapper).toResponseDto(mockCategory);
    }

    @Test
    @DisplayName("getCategoryById - Categoría no encontrada, lanza excepción")
    void testGetCategoryById_NotFound_ThrowsException() {
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999))
                .isInstanceOf(ProductException.class);

        verify(categoryRepository).findById(999);
        verify(categoryMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("getCategoryByName - Encontrar categoría por nombre")
    void testGetCategoryByName_Success() {
        when(categoryRepository.findByNombre("Electrónicos")).thenReturn(Optional.of(mockCategory));
        when(categoryMapper.toResponseDto(mockCategory)).thenReturn(mockResponseDto);

        CategoryResponseDto result = categoryService.getCategoryByName("Electrónicos");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electrónicos");

        verify(categoryRepository).findByNombre("Electrónicos");
        verify(categoryMapper).toResponseDto(mockCategory);
    }

    @Test
    @DisplayName("getCategoryByName - Categoría no encontrada, lanza excepción")
    void testGetCategoryByName_NotFound_ThrowsException() {
        when(categoryRepository.findByNombre("NoExiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryByName("NoExiste"))
                .isInstanceOf(ProductException.class);

        verify(categoryRepository).findByNombre("NoExiste");
        verify(categoryMapper, never()).toResponseDto(any());
    }
}

