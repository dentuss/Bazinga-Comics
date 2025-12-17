package com.bazinga.bazingabe.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ComicRepository comicRepository;
    private final UserRepository userRepository;

    public CartController(CartRepository cartRepository, CartItemRepository cartItemRepository,
            ComicRepository comicRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.comicRepository = comicRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
        return ResponseEntity.ok(cartItemRepository.findAll().stream().filter(i -> i.getCart().getId().equals(cart.getId()))
                .toList());
    }

    @PostMapping
    public ResponseEntity<List<CartItem>> addToCart(Authentication authentication, @RequestBody CartItemRequest request) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        Comic comic = comicRepository.findById(request.getComicId()).orElseThrow();
        CartItem cartItem = cartItemRepository.findByCartAndComic(cart, comic).orElseGet(() -> {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setComic(comic);
            newItem.setQuantity(0);
            return newItem;
        });
        cartItem.setQuantity(cartItem.getQuantity() + (request.getQuantity() == null ? 1 : request.getQuantity()));
        cartItemRepository.save(cartItem);
        return getCart(authentication);
    }

    @PutMapping
    public ResponseEntity<List<CartItem>> updateQuantity(Authentication authentication,
            @RequestBody CartItemRequest request) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseThrow();
        Comic comic = comicRepository.findById(request.getComicId()).orElseThrow();
        CartItem item = cartItemRepository.findByCartAndComic(cart, comic).orElseThrow();
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return getCart(authentication);
    }

    @DeleteMapping("/{comicId}")
    public ResponseEntity<List<CartItem>> removeItem(Authentication authentication, @PathVariable Long comicId) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseThrow();
        Comic comic = comicRepository.findById(comicId).orElseThrow();
        cartItemRepository.findByCartAndComic(cart, comic).ifPresent(cartItemRepository::delete);
        return getCart(authentication);
    }
}
