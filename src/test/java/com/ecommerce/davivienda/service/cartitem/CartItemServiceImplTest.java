package com.ecommerce.davivienda.service.cartitem;

import com.ecommerce.davivienda.dto.cart.summary.CartSummaryDto;
import com.ecommerce.davivienda.entity.cart.Cart;
import com.ecommerce.davivienda.entity.cart.CartItem;
import com.ecommerce.davivienda.entity.product.Product;
import com.ecommerce.davivienda.mapper.cart.CartItemMapper;
import com.ecommerce.davivienda.models.Response;
import com.ecommerce.davivienda.models.cart.CartItemRequest;
import com.ecommerce.davivienda.service.auth.AuthUserService;
import com.ecommerce.davivienda.service.cartitem.transactional.cart.CartItemCartTransactionalService;
import com.ecommerce.davivienda.service.cartitem.validation.cart.CartItemCartValidationService;
import com.ecommerce.davivienda.service.cartitem.validation.common.CartItemCommonValidationService;
import com.ecommerce.davivienda.service.cartitem.validation.product.CartItemProductValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.davivienda.constants.Constants.SUCCESS_CART_ITEM_ADDED;
import static com.ecommerce.davivienda.constants.Constants.SUCCESS_CART_ITEM_DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartItemServiceImpl - Tests Unitarios")
class CartItemServiceImplTest {

    @Mock
    private CartItemCartTransactionalService transactionalService;

    @Mock
    private CartItemCartValidationService cartValidationService;

    @Mock
    private CartItemProductValidationService productValidationService;

    @Mock
    private CartItemCommonValidationService commonValidationService;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private AuthUserService authUserService;

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    private Cart mockCart;
    private Product mockProduct;
    private CartItem mockCartItem;
    private CartItemRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockCart = new Cart();
        mockCart.setCarritoId(1);
        mockCart.setUsuarioRolId(100);

        mockProduct = new Product();
        mockProduct.setProductoId(1);
        mockProduct.setNombre("Laptop");
        mockProduct.setValorUnitario(new BigDecimal("1000"));
        mockProduct.setIva(new BigDecimal("19"));
        mockProduct.setEstadoProductoId(1);

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
    @DisplayName("addItemToCart - Agregar nuevo item al carrito")
    void testAddItemToCart_NewItem_Success() {
        when(authUserService.getAuthenticatedUserRoleId()).thenReturn(100);
        when(transactionalService.findOrCreateCart(100)).thenReturn(mockCart);
        when(productValidationService.validateProductExists(1)).thenReturn(mockProduct);
        doNothing().when(productValidationService).validateProductActive(mockProduct);
        doNothing().when(commonValidationService).validateQuantity(2);
        when(transactionalService.findCartItemByCartAndProduct(1, 1)).thenReturn(Optional.empty());
        when(cartItemMapper.toEntity(mockRequest, mockCart, mockProduct)).thenReturn(mockCartItem);
        when(transactionalService.saveCartItem(any(CartItem.class))).thenReturn(mockCartItem);

        Response<String> result = cartItemService.addItemToCart(mockRequest);

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isEqualTo(SUCCESS_CART_ITEM_ADDED);
        assertThat(result.getFailure()).isFalse();

        verify(authUserService).getAuthenticatedUserRoleId();
        verify(transactionalService).findOrCreateCart(100);
        verify(productValidationService).validateProductExists(1);
        verify(productValidationService).validateProductActive(mockProduct);
        verify(commonValidationService).validateQuantity(2);
        verify(transactionalService).saveCartItem(any(CartItem.class));
    }

    @Test
    @DisplayName("addItemToCart - Actualizar item existente en carrito")
    void testAddItemToCart_UpdateExisting_Success() {
        when(authUserService.getAuthenticatedUserRoleId()).thenReturn(100);
        when(transactionalService.findOrCreateCart(100)).thenReturn(mockCart);
        when(productValidationService.validateProductExists(1)).thenReturn(mockProduct);
        doNothing().when(productValidationService).validateProductActive(mockProduct);
        doNothing().when(commonValidationService).validateQuantity(2);
        when(transactionalService.findCartItemByCartAndProduct(1, 1)).thenReturn(Optional.of(mockCartItem));
        doNothing().when(cartItemMapper).updateQuantity(mockCartItem, 2);
        when(transactionalService.saveCartItem(mockCartItem)).thenReturn(mockCartItem);

        Response<String> result = cartItemService.addItemToCart(mockRequest);

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isEqualTo(SUCCESS_CART_ITEM_ADDED);

        verify(cartItemMapper).updateQuantity(mockCartItem, 2);
        verify(transactionalService).saveCartItem(mockCartItem);
    }

    @Test
    @DisplayName("removeItemFromCart - Eliminar item del carrito")
    void testRemoveItemFromCart_Success() {
        when(authUserService.getAuthenticatedUserRoleId()).thenReturn(100);
        when(cartValidationService.validateItemBelongsToUser(1, 100)).thenReturn(mockCartItem);
        doNothing().when(transactionalService).deleteCartItem(mockCartItem);

        Response<String> result = cartItemService.removeItemFromCart(1);

        assertThat(result).isNotNull();
        assertThat(result.getBody()).isEqualTo(SUCCESS_CART_ITEM_DELETED);

        verify(authUserService).getAuthenticatedUserRoleId();
        verify(cartValidationService).validateItemBelongsToUser(1, 100);
        verify(transactionalService).deleteCartItem(mockCartItem);
    }

    @Test
    @DisplayName("getCartSummary - Obtener resumen del carrito")
    void testGetCartSummary_Success() {
        List<CartItem> items = Arrays.asList(mockCartItem);
        CartSummaryDto mockSummary = CartSummaryDto.builder().build();

        when(authUserService.getAuthenticatedUserRoleId()).thenReturn(100);
        when(transactionalService.findOrCreateCart(100)).thenReturn(mockCart);
        when(transactionalService.findCartItemsByCartId(1)).thenReturn(items);
        when(cartItemMapper.toCartSummaryDto(items)).thenReturn(mockSummary);

        CartSummaryDto result = cartItemService.getCartSummary();

        assertThat(result).isNotNull();

        verify(authUserService).getAuthenticatedUserRoleId();
        verify(transactionalService).findOrCreateCart(100);
        verify(transactionalService).findCartItemsByCartId(1);
        verify(cartItemMapper).toCartSummaryDto(items);
    }
}

