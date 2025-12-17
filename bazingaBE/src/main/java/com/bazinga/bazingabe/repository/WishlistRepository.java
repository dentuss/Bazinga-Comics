package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.User;
import com.bazinga.bazingabe.entity.Wishlist;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUser(User user);
}
