package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.Cart;
import com.bazinga.bazingabe.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
