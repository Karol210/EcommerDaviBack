package com.ecommerce.davivienda.service.product.transactional.product;

import com.ecommerce.davivienda.entity.product.Product;
import com.ecommerce.davivienda.repository.product.ProductRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductProductTransactionalServiceImpl - Tests Unitarios")
class ProductProductTransactionalServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductProductTransactionalServiceImpl transactionalService;

    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setProductoId(1);
        mockProduct.setNombre("Laptop");
        mockProduct.setValorUnitario(new BigDecimal("1000"));
    }

    @Test
    @DisplayName("findProductById - Producto existe")
    void testFindProductById_Exists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));

        Optional<Product> result = transactionalService.findProductById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getProductoId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findProductById - Producto no existe")
    void testFindProductById_NotExists() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Product> result = transactionalService.findProductById(999);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllProducts - Retorna lista de productos")
    void testFindAllProducts_Success() {
        List<Product> products = Arrays.asList(mockProduct);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = transactionalService.findAllProducts();

        assertThat(result).isNotNull().hasSize(1);
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("findProductsByStatus - Retorna productos por estado")
    void testFindProductsByStatus_Success() {
        List<Product> products = Arrays.asList(mockProduct);
        when(productRepository.findByEstadoProductoId(1)).thenReturn(products);

        List<Product> result = transactionalService.findProductsByStatus(1);

        assertThat(result).isNotNull().hasSize(1);
        verify(productRepository).findByEstadoProductoId(1);
    }

    @Test
    @DisplayName("findAllProducts - Con Specification")
    void testFindAllProducts_WithSpec_Success() {
        Specification<Product> spec = mock(Specification.class);
        List<Product> products = Arrays.asList(mockProduct);
        when(productRepository.findAll(spec)).thenReturn(products);

        List<Product> result = transactionalService.findAllProducts(spec);

        assertThat(result).isNotNull().hasSize(1);
        verify(productRepository).findAll(spec);
    }

    @Test
    @DisplayName("findAllProducts - Con Specification y Pageable")
    void testFindAllProducts_WithSpecAndPageable_Success() {
        Specification<Product> spec = mock(Specification.class);
        Pageable pageable = mock(Pageable.class);
        Page<Product> productsPage = new PageImpl<>(Arrays.asList(mockProduct));
        
        when(productRepository.findAll(spec, pageable)).thenReturn(productsPage);

        Page<Product> result = transactionalService.findAllProducts(spec, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findAll(spec, pageable);
    }

    @Test
    @DisplayName("existsByName - Producto existe por nombre")
    void testExistsByName_Exists() {
        when(productRepository.existsByNombre("Laptop")).thenReturn(true);

        boolean result = transactionalService.existsByName("Laptop");

        assertThat(result).isTrue();
        verify(productRepository).existsByNombre("Laptop");
    }

    @Test
    @DisplayName("existsByName - Producto no existe por nombre")
    void testExistsByName_NotExists() {
        when(productRepository.existsByNombre("NoExiste")).thenReturn(false);

        boolean result = transactionalService.existsByName("NoExiste");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("existsByNameAndNotId - Producto existe excluyendo ID")
    void testExistsByNameAndNotId_Exists() {
        when(productRepository.existsByNombreAndProductoIdNot("Laptop", 2)).thenReturn(true);

        boolean result = transactionalService.existsByNameAndNotId("Laptop", 2);

        assertThat(result).isTrue();
        verify(productRepository).existsByNombreAndProductoIdNot("Laptop", 2);
    }

    @Test
    @DisplayName("saveProduct - Guarda producto exitosamente")
    void testSaveProduct_Success() {
        when(productRepository.save(mockProduct)).thenReturn(mockProduct);

        Product result = transactionalService.saveProduct(mockProduct);

        assertThat(result).isNotNull();
        assertThat(result.getProductoId()).isEqualTo(1);
        verify(productRepository).save(mockProduct);
    }
}

