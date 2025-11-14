package com.ecommerce.davivienda.service.product;

import com.ecommerce.davivienda.dto.product.ProductFilterDto;
import com.ecommerce.davivienda.entity.product.Category;
import com.ecommerce.davivienda.entity.product.Product;
import com.ecommerce.davivienda.mapper.product.ProductMapper;
import com.ecommerce.davivienda.models.product.ProductRequest;
import com.ecommerce.davivienda.models.product.ProductResponse;
import com.ecommerce.davivienda.models.product.ProductUpdateRequest;
import com.ecommerce.davivienda.service.product.transactional.product.ProductProductTransactionalService;
import com.ecommerce.davivienda.service.product.validation.category.ProductCategoryValidationService;
import com.ecommerce.davivienda.service.product.validation.common.ProductCommonValidationService;
import com.ecommerce.davivienda.service.product.validation.product.ProductProductValidationService;
import com.ecommerce.davivienda.service.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl - Tests Unitarios")
class ProductServiceImplTest {

    @Mock
    private ProductProductValidationService productValidationService;

    @Mock
    private ProductCategoryValidationService categoryValidationService;

    @Mock
    private ProductCommonValidationService commonValidationService;

    @Mock
    private ProductProductTransactionalService transactionalService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private StockService stockService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product mockProduct;
    private Category mockCategory;
    private ProductRequest mockRequest;
    private ProductResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockCategory = new Category();
        mockCategory.setCategoriaId(1);
        mockCategory.setNombre("Electrónicos");

        mockProduct = new Product();
        mockProduct.setProductoId(1);
        mockProduct.setNombre("Laptop");
        mockProduct.setValorUnitario(new BigDecimal("1000"));
        mockProduct.setIva(new BigDecimal("19"));
        mockProduct.setCategoria(mockCategory);

        mockRequest = ProductRequest.builder()
                .name("Laptop")
                .unitValue(new BigDecimal("1000"))
                .iva(new BigDecimal("19"))
                .categoryName("Electrónicos")
                .inventory(10)
                .build();

        mockResponse = ProductResponse.builder()
                .id(1)
                .name("Laptop")
                .build();
    }

    @Test
    @DisplayName("createProduct - Crear producto con inventario")
    void testCreateProduct_WithInventory_Success() {
        doNothing().when(productValidationService).validateProductNameNotExists("Laptop");
        doNothing().when(commonValidationService).validatePrices(mockRequest);
        when(categoryValidationService.findCategoryByNameOrThrow("Electrónicos")).thenReturn(mockCategory);
        doNothing().when(categoryValidationService).validateCategoryActive(mockCategory);
        when(productMapper.toEntity(mockRequest)).thenReturn(mockProduct);
        when(transactionalService.saveProduct(any(Product.class))).thenReturn(mockProduct);
        when(productMapper.toResponseDto(mockProduct)).thenReturn(mockResponse);
        doNothing().when(stockService).createOrUpdateStock(1, 10);

        ProductResponse result = productService.createProduct(mockRequest);

        assertThat(result).isNotNull();
        verify(transactionalService).saveProduct(any(Product.class));
        verify(stockService).createOrUpdateStock(1, 10);
    }

    @Test
    @DisplayName("getProductById - Obtener producto por ID")
    void testGetProductById_Success() {
        when(productValidationService.findProductByIdOrThrow(1)).thenReturn(mockProduct);
        when(productMapper.toResponseDto(mockProduct)).thenReturn(mockResponse);

        ProductResponse result = productService.getProductById(1);

        assertThat(result).isNotNull();
        verify(productValidationService).findProductByIdOrThrow(1);
    }

    @Test
    @DisplayName("getAllProducts - Listar todos los productos")
    void testGetAllProducts_Success() {
        List<Product> products = Arrays.asList(mockProduct);
        when(transactionalService.findAllProducts()).thenReturn(products);
        when(productMapper.toResponseDto(mockProduct)).thenReturn(mockResponse);

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).isNotNull().hasSize(1);
        verify(transactionalService).findAllProducts();
    }

    @Test
    @DisplayName("getActiveProducts - Listar productos activos")
    void testGetActiveProducts_Success() {
        List<Product> products = Arrays.asList(mockProduct);
        when(transactionalService.findProductsByStatus(1)).thenReturn(products);
        when(productMapper.toResponseDto(mockProduct)).thenReturn(mockResponse);

        List<ProductResponse> result = productService.getActiveProducts();

        assertThat(result).isNotNull().hasSize(1);
        verify(transactionalService).findProductsByStatus(1);
    }

    @Test
    @DisplayName("searchProducts - Buscar productos con filtros")
    void testSearchProducts_Success() {
        ProductFilterDto filter = ProductFilterDto.builder().build();
        Specification<Product> spec = mock(Specification.class);
        List<Product> products = Arrays.asList(mockProduct);

        when(productMapper.buildSpecificationFromFilter(filter)).thenReturn(spec);
        when(transactionalService.findAllProducts(spec)).thenReturn(products);
        when(productMapper.toResponseDto(mockProduct)).thenReturn(mockResponse);

        List<ProductResponse> result = productService.searchProducts(filter);

        assertThat(result).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("searchProductsPaginated - Buscar productos paginados")
    void testSearchProductsPaginated_Success() {
        Specification<Product> spec = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);
        Page<Product> productsPage = new PageImpl<>(Arrays.asList(mockProduct));

        when(productMapper.buildSpecificationFromParams(any(), any(), any(), any(), any())).thenReturn(spec);
        when(productMapper.buildPageable(0, 10, "nombre", "asc")).thenReturn(pageable);
        when(transactionalService.findAllProducts(spec, pageable)).thenReturn(productsPage);
        when(productMapper.toResponseDto(mockProduct)).thenReturn(mockResponse);

        Page<ProductResponse> result = productService.searchProductsPaginated(
                null, null, null, null, null, 0, 10, "nombre", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("updateProduct - Actualizar producto")
    void testUpdateProduct_Success() {
        ProductUpdateRequest updateRequest = ProductUpdateRequest.builder()
                .name("Laptop Updated")
                .build();

        when(productValidationService.findProductByIdOrThrow(1)).thenReturn(mockProduct);
        doNothing().when(productValidationService).validateProductNameNotExistsOnUpdate("Laptop Updated", 1);
        doNothing().when(productMapper).updateEntityFromDto(updateRequest, mockProduct);
        when(transactionalService.saveProduct(mockProduct)).thenReturn(mockProduct);

        productService.updateProduct(1, updateRequest);

        verify(transactionalService).saveProduct(mockProduct);
    }
}

