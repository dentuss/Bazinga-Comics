package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.Cart;
import com.bazinga.bazingabe.entity.CartItem;
import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.PurchaseType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndComicAndPurchaseType(Cart cart, Comic comic, PurchaseType purchaseType);
    void deleteByCart(Cart cart);
    void deleteByComic(Comic comic);

    @Transactional
    @Modifying
    @Query("delete from CartItem item where item.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);
}
