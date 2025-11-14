package com.ecommerce.davivienda.service.cartitem.transactional.cart;

import com.ecommerce.davivienda.entity.cart.Cart;
import com.ecommerce.davivienda.entity.cart.CartItem;
import com.ecommerce.davivienda.repository.cart.CartItemRepository;
import com.ecommerce.davivienda.repository.cart.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.davivienda.constants.Constants.CART_STATUS_ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartItemCartTransactionalServiceImpl - Tests Unitarios")
class CartItemCartTransactionalServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartItemCartTransactionalServiceImpl transactionalService;

    private Cart mockCart;
    private CartItem mockCartItem;

    @BeforeEach
    void setUp() {
        mockCart = new Cart();
        mockCart.setCarritoId(1);
        mockCart.setUsuarioRolId(100);
        mockCart.setEstadoCarritoId(CART_STATUS_ACTIVE);

        mockCartItem = new CartItem();
        mockCartItem.setProductosCarritoId(1);
        mockCartItem.setCart(mockCart);
        mockCartItem.setCantidad(2);
    }

    @Test
    @DisplayName("findOrCreateCart - Carrito existe, retorna existente")
    void testFindOrCreateCart_Exists_ReturnsExisting() {
        when(cartRepository.findByUsuarioRolIdAndEstadoCarritoId(100, CART_STATUS_ACTIVE))
                .thenReturn(Optional.of(mockCart));

        Cart result = transactionalService.findOrCreateCart(100);

        assertThat(result).isNotNull();
        assertThat(result.getCarritoId()).isEqualTo(1);
        verify(cartRepository).findByUsuarioRolIdAndEstadoCarritoId(100, CART_STATUS_ACTIVE);
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("findOrCreateCart - Carrito no existe, crea nuevo")
    void testFindOrCreateCart_NotExists_CreatesNew() {
        when(cartRepository.findByUsuarioRolIdAndEstadoCarritoId(100, CART_STATUS_ACTIVE))
                .thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        Cart result = transactionalService.findOrCreateCart(100);

        assertThat(result).isNotNull();
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("findCartItemByCartAndProduct - Item existe")
    void testFindCartItemByCartAndProduct_Exists() {
        when(cartItemRepository.findByCartAndProduct(1, 1)).thenReturn(Optional.of(mockCartItem));

        Optional<CartItem> result = transactionalService.findCartItemByCartAndProduct(1, 1);

        assertThat(result).isPresent();
        assertThat(result.get().getProductosCarritoId()).isEqualTo(1);
    }

    @Test
    @DisplayName("findCartItemsByCartId - Retorna lista de items")
    void testFindCartItemsByCartId_Success() {
        List<CartItem> items = Arrays.asList(mockCartItem);
        when(cartItemRepository.findByCartCarritoId(1)).thenReturn(items);

        List<CartItem> result = transactionalService.findCartItemsByCartId(1);

        assertThat(result).isNotNull().hasSize(1);
        verify(cartItemRepository).findByCartCarritoId(1);
    }

    @Test
    @DisplayName("saveCartItem - Guarda item exitosamente")
    void testSaveCartItem_Success() {
        when(cartItemRepository.save(mockCartItem)).thenReturn(mockCartItem);

        CartItem result = transactionalService.saveCartItem(mockCartItem);

        assertThat(result).isNotNull();
        verify(cartItemRepository).save(mockCartItem);
    }

    @Test
    @DisplayName("deleteCartItem - Elimina item exitosamente")
    void testDeleteCartItem_Success() {
        doNothing().when(cartItemRepository).delete(mockCartItem);

        transactionalService.deleteCartItem(mockCartItem);

        verify(cartItemRepository).delete(mockCartItem);
    }

    @Test
    @DisplayName("findCartItemByIdAndUser - Item existe para usuario")
    void testFindCartItemByIdAndUser_Success() {
        when(cartItemRepository.findByProductIdAndUserRole(1, 100)).thenReturn(Optional.of(mockCartItem));

        Optional<CartItem> result = transactionalService.findCartItemByIdAndUser(1, 100);

        assertThat(result).isPresent();
        verify(cartItemRepository).findByProductIdAndUserRole(1, 100);
    }
}

