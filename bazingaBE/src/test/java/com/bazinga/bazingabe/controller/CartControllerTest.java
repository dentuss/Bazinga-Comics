package com.bazinga.bazingabe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bazinga.bazingabe.dto.CartItemRequest;
import com.bazinga.bazingabe.entity.Cart;
import com.bazinga.bazingabe.entity.CartItem;
import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.User;
import com.bazinga.bazingabe.repository.CartItemRepository;
import com.bazinga.bazingabe.repository.CartRepository;
import com.bazinga.bazingabe.repository.ComicRepository;
import com.bazinga.bazingabe.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ComicRepository comicRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartController cartController;

    @Test
    void getCartReturnsItemsForAuthenticatedUser() {
        User user = createUser(3L, "shopper@example.com");
        Cart cart = createCart(11L, user);
        Cart anotherCart = createCart(22L, user);

        CartItem cartItem = createCartItem(101L, cart, 2, 101L);
        CartItem otherItem = createCartItem(102L, anotherCart, 5, 202L);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), "password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAll()).thenReturn(List.of(cartItem, otherItem));

        var response = cartController.getCart(authentication);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(1, response.getBody().size());
        assertEquals(101L, response.getBody().getFirst().getId());
        assertEquals(2, response.getBody().getFirst().getQuantity());
    }

    @Test
    void addToCartCreatesItemAndReturnsUpdatedCart() {
        User user = createUser(4L, "buyer@example.com");
        Cart cart = createCart(31L, user);
        Comic comic = new Comic();
        comic.setId(7L);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), "password");

        CartItem cartItem = createCartItem(201L, cart, 0, comic.getId());

        CartItemRequest request = new CartItemRequest();
        request.setComicId(comic.getId());
        request.setQuantity(3);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(comicRepository.findById(comic.getId())).thenReturn(Optional.of(comic));
        when(cartItemRepository.findByCartAndComic(cart, comic)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartItemRepository.findAll()).thenAnswer(invocation -> {
            cartItem.setQuantity(request.getQuantity());
            return List.of(cartItem);
        });

        var response = cartController.addToCart(authentication, request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(1, response.getBody().size());
        assertEquals(3, response.getBody().getFirst().getQuantity());
        assertEquals(7L, response.getBody().getFirst().getComic().getId());
        verify(cartItemRepository).save(any(CartItem.class));
    }

    private User createUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setUsername(email);
        return user;
    }

    private Cart createCart(Long id, User user) {
        Cart cart = new Cart();
        cart.setId(id);
        cart.setUser(user);
        return cart;
    }

    private CartItem createCartItem(Long id, Cart cart, Integer quantity, Long comicId) {
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setCart(cart);
        cartItem.setComic(new Comic());
        cartItem.getComic().setId(comicId);
        cartItem.setQuantity(quantity);
        return cartItem;
    }
}