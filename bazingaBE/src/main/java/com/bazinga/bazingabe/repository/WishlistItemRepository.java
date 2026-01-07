package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.Wishlist;
import com.bazinga.bazingabe.entity.WishlistItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    Optional<WishlistItem> findByWishlistAndComic(Wishlist wishlist, Comic comic);
    void deleteByWishlist(Wishlist wishlist);
    void deleteByComic(Comic comic);
}
