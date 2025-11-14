package com.ecommerce.davivienda.controller.product;

import com.ecommerce.davivienda.dto.product.PagedProductResponseDto;
import com.ecommerce.davivienda.dto.product.ProductFilterDto;
import com.ecommerce.davivienda.models.Response;
import com.ecommerce.davivienda.models.product.ProductRequest;
import com.ecommerce.davivienda.models.product.ProductResponse;
import com.ecommerce.davivienda.models.product.ProductUpdateRequest;
import com.ecommerce.davivienda.service.product.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests unitarios para ProductController.
 *
 * @author Team Ecommerce Davivienda
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController - Tests Unitarios")
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ProductResponse mockProductResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

        mockProductResponse = ProductResponse.builder()
                .id(1)
                .name("Producto Test")
                .description("Descripción Test")
                .unitValue(BigDecimal.valueOf(99.99))
                .estadoProductoId(1)  // 1 = Activo
                .build();
    }

    @Test
    @DisplayName("POST /create - Crear producto exitosamente")
    void testCreateProduct_Success() throws Exception {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Nuevo Producto")
                .description("Descripción")
                .unitValue(BigDecimal.valueOf(99.99))
                .categoryName("Electrónicos")
                .build();

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(mockProductResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(201));

        verify(productService).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("GET /get-by-id/{id} - Obtener producto por ID exitosamente")
    void testGetProductById_Success() throws Exception {
        // Arrange
        when(productService.getProductById(1)).thenReturn(mockProductResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/get-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body.name").value("Producto Test"));

        verify(productService).getProductById(1);
    }

    @Test
    @DisplayName("GET /list-all - Listar todos los productos")
    void testGetAllProducts_Success() throws Exception {
        // Arrange
        List<ProductResponse> products = Arrays.asList(mockProductResponse, mockProductResponse);
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/list-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body").isArray())
                .andExpect(jsonPath("$.body.length()").value(2));

        verify(productService).getAllProducts();
    }

    @Test
    @DisplayName("GET /list-active - Listar productos activos")
    void testGetActiveProducts_Success() throws Exception {
        // Arrange
        List<ProductResponse> products = Arrays.asList(mockProductResponse);
        when(productService.getActiveProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/list-active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body").isArray());

        verify(productService).getActiveProducts();
    }

    @Test
    @DisplayName("POST /search - Buscar productos con filtros")
    void testSearchProducts_Success() throws Exception {
        // Arrange
        ProductFilterDto filter = ProductFilterDto.builder()
                .searchTerm("Test")
                .active(true)
                .build();

        List<ProductResponse> products = Arrays.asList(mockProductResponse);
        when(productService.searchProducts(any(ProductFilterDto.class))).thenReturn(products);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body").isArray());

        verify(productService).searchProducts(any(ProductFilterDto.class));
    }

    @Test
    @DisplayName("GET /search/paginated - Buscar productos con paginación")
    void testSearchProductsPaginated_Success() throws Exception {
        // Arrange
        Page<ProductResponse> page = new PageImpl<>(Arrays.asList(mockProductResponse));
        when(productService.searchProductsPaginated(
                anyInt(), any(), any(), any(), anyString(),
                anyInt(), anyInt(), anyString(), anyString()
        )).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/search/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "productoId")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.body").exists());

        verify(productService).searchProductsPaginated(
                any(), any(), any(), any(), any(),
                anyInt(), anyInt(), anyString(), anyString()
        );
    }

    @Test
    @DisplayName("PUT /update/{id} - Actualizar producto")
    void testUpdateProduct_Success() throws Exception {
        // Arrange
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("Producto Actualizado")
                .unitValue(BigDecimal.valueOf(149.99))
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/v1/products/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.failure").value(false))
                .andExpect(jsonPath("$.code").value(200));

        verify(productService).updateProduct(eq(1), any(ProductUpdateRequest.class));
    }
}

