package com.ecommerce.davivienda.service.cartitem.validation.cart;

import com.ecommerce.davivienda.entity.cart.Cart;
import com.ecommerce.davivienda.entity.cart.CartItem;
import com.ecommerce.davivienda.exception.cart.CartException;
import com.ecommerce.davivienda.service.cartitem.transactional.cart.CartItemCartTransactionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartItemCartValidationServiceImpl - Tests Unitarios")
class CartItemCartValidationServiceImplTest {

    @Mock
    private CartItemCartTransactionalService transactionalService;

    @InjectMocks
    private CartItemCartValidationServiceImpl validationService;

    private CartItem mockCartItem;
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        mockCart = new Cart();
        mockCart.setCarritoId(1);
        mockCart.setUsuarioRolId(100);

        mockCartItem = new CartItem();
        mockCartItem.setProductosCarritoId(1);
        mockCartItem.setCart(mockCart);
    }

    @Test
    @DisplayName("validateItemBelongsToUser - Item pertenece al usuario, retorna item")
    void testValidateItemBelongsToUser_BelongsToUser_Success() {
        when(transactionalService.findCartItemByIdAndUser(1, 100)).thenReturn(Optional.of(mockCartItem));

        CartItem result = validationService.validateItemBelongsToUser(1, 100);

        assertThat(result).isNotNull();
        assertThat(result.getProductosCarritoId()).isEqualTo(1);
        verify(transactionalService).findCartItemByIdAndUser(1, 100);
    }

    @Test
    @DisplayName("validateItemBelongsToUser - Item no pertenece al usuario, lanza excepción")
    void testValidateItemBelongsToUser_NotBelongsToUser_ThrowsException() {
        when(transactionalService.findCartItemByIdAndUser(1, 100)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validationService.validateItemBelongsToUser(1, 100))
                .isInstanceOf(CartException.class);

        verify(transactionalService).findCartItemByIdAndUser(1, 100);
    }

    @Test
    @DisplayName("validateItemBelongsToUser - ItemId null, lanza excepción")
    void testValidateItemBelongsToUser_NullItemId_ThrowsException() {
        assertThatThrownBy(() -> validationService.validateItemBelongsToUser(null, 100))
                .isInstanceOf(CartException.class);

        verify(transactionalService, never()).findCartItemByIdAndUser(any(), any());
    }

    @Test
    @DisplayName("validateItemBelongsToUser - UserRoleId null, lanza excepción")
    void testValidateItemBelongsToUser_NullUserRoleId_ThrowsException() {
        assertThatThrownBy(() -> validationService.validateItemBelongsToUser(1, null))
                .isInstanceOf(CartException.class);

        verify(transactionalService, never()).findCartItemByIdAndUser(any(), any());
    }
}

