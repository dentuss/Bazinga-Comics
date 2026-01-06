package com.bazinga.bazingabe.controller;

import com.bazinga.bazingabe.dto.CartItemRequest;
import com.bazinga.bazingabe.entity.Cart;
import com.bazinga.bazingabe.entity.CartItem;
import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.ComicType;
import com.bazinga.bazingabe.entity.PurchaseType;
import com.bazinga.bazingabe.entity.User;
import com.bazinga.bazingabe.repository.CartItemRepository;
import com.bazinga.bazingabe.repository.CartRepository;
import com.bazinga.bazingabe.repository.ComicRepository;
import com.bazinga.bazingabe.repository.UserRepository;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        PurchaseType purchaseType = PurchaseType.fromValue(request.getPurchaseType());
        if (comic.getComicType() == ComicType.ONLY_DIGITAL) {
            purchaseType = PurchaseType.DIGITAL;
        }
        PurchaseType finalPurchaseType = purchaseType;
        CartItem cartItem = cartItemRepository.findByCartAndComicAndPurchaseType(cart, comic, purchaseType)
                .orElseGet(() -> {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setComic(comic);
            newItem.setPurchaseType(finalPurchaseType);
            newItem.setQuantity(0);
            return newItem;
        });
        cartItem.setUnitPrice(calculateUnitPrice(user, comic, purchaseType));
        cartItem.setQuantity(cartItem.getQuantity() + (request.getQuantity() == null ? 1 : request.getQuantity()));
        cartItemRepository.save(cartItem);
        return getCart(authentication);
    }

    @PutMapping
    public ResponseEntity<List<CartItem>> updateQuantity(Authentication authentication,
            @RequestBody CartItemRequest request) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseThrow();
        if (request.getCartItemId() == null) {
            return ResponseEntity.badRequest().build();
        }
        CartItem item = cartItemRepository.findById(request.getCartItemId()).orElseThrow();
        if (!item.getCart().getId().equals(cart.getId())) {
            return ResponseEntity.notFound().build();
        }
        Integer quantity = request.getQuantity();
        if (quantity == null || quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        return getCart(authentication);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<List<CartItem>> removeItem(Authentication authentication, @PathVariable Long cartItemId) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseThrow();
        cartItemRepository.findById(cartItemId)
                .filter(item -> item.getCart().getId().equals(cart.getId()))
                .ifPresent(cartItemRepository::delete);
        return getCart(authentication);
    }

    @DeleteMapping
    public ResponseEntity<List<CartItem>> clearCart(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
        cartItemRepository.deleteByCart(cart);
        return getCart(authentication);
    }

    private BigDecimal calculateUnitPrice(User user, Comic comic, PurchaseType purchaseType) {
        BigDecimal basePrice = comic.getPrice() == null ? BigDecimal.ZERO : comic.getPrice();
        boolean isUnlimited = "unlimited".equalsIgnoreCase(user.getSubscriptionType());
        if (purchaseType == PurchaseType.DIGITAL) {
            if (isUnlimited) {
                return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            }
            return basePrice.multiply(new BigDecimal("0.75")).setScale(2, RoundingMode.HALF_UP);
        }
        if (isUnlimited) {
            return basePrice.multiply(new BigDecimal("0.5")).setScale(2, RoundingMode.HALF_UP);
        }
        return basePrice.setScale(2, RoundingMode.HALF_UP);
    }
}
