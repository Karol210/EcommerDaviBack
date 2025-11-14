package com.ecommerce.davivienda.mapper.category;

import com.ecommerce.davivienda.dto.category.CategoryResponseDto;
import com.ecommerce.davivienda.entity.product.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CategoryMapper - Tests Unitarios")
class CategoryMapperTest {

    private CategoryMapper categoryMapper;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        categoryMapper = Mappers.getMapper(CategoryMapper.class);

        mockCategory = new Category();
        mockCategory.setCategoriaId(1);
        mockCategory.setNombre("Electrónicos");
        mockCategory.setDescripcion("Productos electrónicos");
    }

    @Test
    @DisplayName("toResponseDto - Convierte Category a CategoryResponseDto")
    void testToResponseDto() {
        CategoryResponseDto result = categoryMapper.toResponseDto(mockCategory);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Electrónicos");
        assertThat(result.getDescription()).isEqualTo("Productos electrónicos");
    }

    @Test
    @DisplayName("toResponseDtoList - Convierte lista de Categories")
    void testToResponseDtoList() {
        List<Category> categories = Arrays.asList(mockCategory);
        List<CategoryResponseDto> result = categoryMapper.toResponseDtoList(categories);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Electrónicos");
    }
}

