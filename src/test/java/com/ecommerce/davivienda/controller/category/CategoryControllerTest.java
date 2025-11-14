package com.ecommerce.davivienda.controller.category;

import com.ecommerce.davivienda.dto.category.CategoryResponseDto;
import com.ecommerce.davivienda.models.Response;
import com.ecommerce.davivienda.service.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests unitarios para CategoryController.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryController - Tests Unitarios")
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private CategoryResponseDto mockCategory;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();

        mockCategory = CategoryResponseDto.builder()
                .id(1)
                .name("Electrónica")
                .description("Productos electrónicos")
                .build();
    }

    @Test
    @DisplayName("GET /list-all - Listar todas las categorías")
    void testGetAllCategories_Success() throws Exception {
        // Arrange
        List<CategoryResponseDto> categories = Arrays.asList(mockCategory, mockCategory);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/list-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body").isArray())
                .andExpect(jsonPath("$.body.length()").value(2))
                .andExpect(jsonPath("$.message").value("Categorías listadas exitosamente"));

        verify(categoryService).getAllCategories();
    }

    @Test
    @DisplayName("GET /find-by-name/{name} - Buscar categoría por nombre")
    void testGetCategoryByName_Success() throws Exception {
        // Arrange
        when(categoryService.getCategoryByName("Electrónica")).thenReturn(mockCategory);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/find-by-name/Electrónica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body.name").value("Electrónica"))
                .andExpect(jsonPath("$.message").value("Categoría encontrada"));

        verify(categoryService).getCategoryByName("Electrónica");
    }
}

