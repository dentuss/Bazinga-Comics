package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.Cart;
import com.bazinga.bazingabe.entity.CartItem;
import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.PurchaseType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndComicAndPurchaseType(Cart cart, Comic comic, PurchaseType purchaseType);
    void deleteByCart(Cart cart);
}
