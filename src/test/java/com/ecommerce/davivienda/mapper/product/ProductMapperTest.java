package com.ecommerce.davivienda.mapper.product;

import com.ecommerce.davivienda.dto.product.ProductFilterDto;
import com.ecommerce.davivienda.entity.product.Category;
import com.ecommerce.davivienda.entity.product.Product;
import com.ecommerce.davivienda.models.product.ProductRequest;
import com.ecommerce.davivienda.models.product.ProductResponse;
import com.ecommerce.davivienda.models.product.ProductUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductMapper - Tests Unitarios")
class ProductMapperTest {

    private ProductMapper productMapper;
    private ProductRequest mockRequest;
    private Product mockProduct;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);

        mockCategory = new Category();
        mockCategory.setCategoriaId(1);
        mockCategory.setNombre("Electrónicos");

        mockRequest = ProductRequest.builder()
                .name("Laptop")
                .description("Laptop HP")
                .unitValue(new BigDecimal("1000"))
                .iva(new BigDecimal("19"))
                .imageUrl("https://image.com/laptop.jpg")
                .categoryName("Electrónicos")
                .estadoProductoId(1)
                .build();

        mockProduct = new Product();
        mockProduct.setProductoId(1);
        mockProduct.setNombre("Laptop");
        mockProduct.setDescripcion("Laptop HP");
        mockProduct.setValorUnitario(new BigDecimal("1000"));
        mockProduct.setIva(new BigDecimal("19"));
        mockProduct.setImagen("https://image.com/laptop.jpg");
        mockProduct.setCategoria(mockCategory);
        mockProduct.setEstadoProductoId(1);
    }

    @Test
    @DisplayName("toEntity - Convierte ProductRequest a Product")
    void testToEntity() {
        Product result = productMapper.toEntity(mockRequest);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Laptop");
        assertThat(result.getDescripcion()).isEqualTo("Laptop HP");
        assertThat(result.getValorUnitario()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(result.getIva()).isEqualByComparingTo(new BigDecimal("19"));
        assertThat(result.getImagen()).isEqualTo("https://image.com/laptop.jpg");
        assertThat(result.getEstadoProductoId()).isEqualTo(1);
    }

    @Test
    @DisplayName("toResponseDto - Convierte Product a ProductResponse")
    void testToResponseDto() {
        ProductResponse result = productMapper.toResponseDto(mockProduct);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getDescription()).isEqualTo("Laptop HP");
        assertThat(result.getUnitValue()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(result.getIva()).isEqualByComparingTo(new BigDecimal("19"));
        assertThat(result.getImageUrl()).isEqualTo("https://image.com/laptop.jpg");
        assertThat(result.getCategoryId()).isEqualTo(1);
        assertThat(result.getCategoryName()).isEqualTo("Electrónicos");
    }

    @Test
    @DisplayName("updateEntityFromDto - Actualiza Product desde ProductUpdateRequest")
    void testUpdateEntityFromDto() {
        ProductUpdateRequest updateRequest = ProductUpdateRequest.builder()
                .name("Laptop Dell")
                .unitValue(new BigDecimal("1200"))
                .build();

        productMapper.updateEntityFromDto(updateRequest, mockProduct);

        assertThat(mockProduct.getNombre()).isEqualTo("Laptop Dell");
        assertThat(mockProduct.getValorUnitario()).isEqualByComparingTo(new BigDecimal("1200"));
        assertThat(mockProduct.getDescripcion()).isEqualTo("Laptop HP"); // No cambia
    }

    @Test
    @DisplayName("buildSpecificationFromFilter - Construye Specification desde FilterDto")
    void testBuildSpecificationFromFilter() {
        ProductFilterDto filter = ProductFilterDto.builder()
                .categoryId(1)
                .minPrice(new BigDecimal("100"))
                .maxPrice(new BigDecimal("2000"))
                .active(true)
                .searchTerm("laptop")
                .build();

        Specification<Product> result = productMapper.buildSpecificationFromFilter(filter);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("buildSpecificationFromParams - Construye Specification desde parámetros")
    void testBuildSpecificationFromParams() {
        Specification<Product> result = productMapper.buildSpecificationFromParams(
                1,
                new BigDecimal("100"),
                new BigDecimal("2000"),
                true,
                "laptop"
        );

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("buildPageable - Construye Pageable con ordenamiento ASC")
    void testBuildPageable_Asc() {
        Pageable result = productMapper.buildPageable(0, 10, "nombre", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getSort().toString()).contains("nombre: ASC");
    }

    @Test
    @DisplayName("buildPageable - Construye Pageable con ordenamiento DESC")
    void testBuildPageable_Desc() {
        Pageable result = productMapper.buildPageable(0, 10, "nombre", "desc");

        assertThat(result).isNotNull();
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getSort().toString()).contains("nombre: DESC");
    }
}

