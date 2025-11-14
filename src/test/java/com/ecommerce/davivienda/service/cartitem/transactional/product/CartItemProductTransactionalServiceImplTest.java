package com.ecommerce.davivienda.service.cartitem.transactional.product;

import com.ecommerce.davivienda.entity.product.Product;
import com.ecommerce.davivienda.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartItemProductTransactionalServiceImpl - Tests Unitarios")
class CartItemProductTransactionalServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemProductTransactionalServiceImpl transactionalService;

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
        assertThat(result.get().getNombre()).isEqualTo("Laptop");
        verify(productRepository).findById(1);
    }

    @Test
    @DisplayName("findProductById - Producto no existe")
    void testFindProductById_NotExists() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Product> result = transactionalService.findProductById(999);

        assertThat(result).isEmpty();
        verify(productRepository).findById(999);
    }
}

