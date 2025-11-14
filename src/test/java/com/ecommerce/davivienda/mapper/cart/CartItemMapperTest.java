package com.ecommerce.davivienda.mapper.cart;

import com.ecommerce.davivienda.dto.cart.item.CartItemCalculationDto;
import com.ecommerce.davivienda.dto.cart.summary.CartSummaryDto;
import com.ecommerce.davivienda.entity.cart.Cart;
import com.ecommerce.davivienda.entity.cart.CartItem;
import com.ecommerce.davivienda.entity.product.Product;
import com.ecommerce.davivienda.models.cart.CartItemRequest;
import com.ecommerce.davivienda.models.cart.CartItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CartItemMapper - Tests Unitarios")
class CartItemMapperTest {

    private CartItemMapper cartItemMapper;
    private Cart mockCart;
    private Product mockProduct;
    private CartItem mockCartItem;
    private CartItemRequest mockRequest;

    @BeforeEach
    void setUp() {
        cartItemMapper = Mappers.getMapper(CartItemMapper.class);

        mockCart = new Cart();
        mockCart.setCarritoId(1);

        mockProduct = new Product();
        mockProduct.setProductoId(1);
        mockProduct.setNombre("Laptop");
        mockProduct.setDescripcion("Laptop HP");
        mockProduct.setValorUnitario(new BigDecimal("1000"));
        mockProduct.setIva(new BigDecimal("19"));
        mockProduct.setImagen("image.jpg");

        mockCartItem = new CartItem();
        mockCartItem.setProductosCarritoId(1);
        mockCartItem.setCart(mockCart);
        mockCartItem.setProduct(mockProduct);
        mockCartItem.setCantidad(2);

        mockRequest = CartItemRequest.builder()
                .productId(1)
                .quantity(2)
                .build();
    }

    @Test
    @DisplayName("toEntity - Convierte CartItemRequest a CartItem")
    void testToEntity() {
        CartItem result = cartItemMapper.toEntity(mockRequest, mockCart, mockProduct);

        assertThat(result).isNotNull();
        assertThat(result.getCart()).isEqualTo(mockCart);
        assertThat(result.getProduct()).isEqualTo(mockProduct);
        assertThat(result.getCantidad()).isEqualTo(2);
    }

    @Test
    @DisplayName("updateQuantity - Actualiza cantidad de CartItem")
    void testUpdateQuantity() {
        cartItemMapper.updateQuantity(mockCartItem, 5);

        assertThat(mockCartItem.getCantidad()).isEqualTo(5);
    }

    @Test
    @DisplayName("toResponseDto - Convierte CartItem a CartItemResponse")
    void testToResponseDto() {
        CartItemResponse result = cartItemMapper.toResponseDto(mockCartItem);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getCartId()).isEqualTo(1);
        assertThat(result.getProductId()).isEqualTo(1);
        assertThat(result.getProductName()).isEqualTo("Laptop");
        assertThat(result.getCalculation()).isNotNull();
    }

    @Test
    @DisplayName("toResponseDtoList - Convierte lista de CartItems")
    void testToResponseDtoList() {
        List<CartItem> items = Arrays.asList(mockCartItem);
        List<CartItemResponse> result = cartItemMapper.toResponseDtoList(items);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("toCartSummaryDto - Genera resumen del carrito")
    void testToCartSummaryDto() {
        List<CartItem> items = Arrays.asList(mockCartItem);
        CartSummaryDto result = cartItemMapper.toCartSummaryDto(items);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getTotalSubtotal()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("buildCalculationDto - Construye DTO de cálculos")
    void testBuildCalculationDto() {
        CartItemCalculationDto result = cartItemMapper.buildCalculationDto(mockCartItem);

        assertThat(result).isNotNull();
        assertThat(result.getUnitValue()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(result.getIvaPercentage()).isEqualByComparingTo(new BigDecimal("19"));
        assertThat(result.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("buildCalculationDto - CartItem null, retorna null")
    void testBuildCalculationDto_NullCartItem() {
        CartItemCalculationDto result = cartItemMapper.buildCalculationDto(null);

        assertThat(result).isNull();
    }
}

