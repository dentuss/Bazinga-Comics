package com.bazinga.bazingabe.controller;

import com.bazinga.bazingabe.dto.WishlistItemRequest;
import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.User;
import com.bazinga.bazingabe.entity.Wishlist;
import com.bazinga.bazingabe.entity.WishlistItem;
import com.bazinga.bazingabe.repository.ComicRepository;
import com.bazinga.bazingabe.repository.UserRepository;
import com.bazinga.bazingabe.repository.WishlistItemRepository;
import com.bazinga.bazingabe.repository.WishlistRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ComicRepository comicRepository;
    private final UserRepository userRepository;

    public WishlistController(WishlistRepository wishlistRepository, WishlistItemRepository wishlistItemRepository,
            ComicRepository comicRepository, UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.comicRepository = comicRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<WishlistItem>> getWishlist(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Wishlist wishlist = wishlistRepository.findByUser(user).orElseGet(() -> {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setUser(user);
            return wishlistRepository.save(newWishlist);
        });
        return ResponseEntity.ok(wishlistItemRepository.findAll().stream()
                .filter(item -> item.getWishlist().getId().equals(wishlist.getId()))
                .toList());
    }

    @PostMapping
    public ResponseEntity<List<WishlistItem>> addToWishlist(Authentication authentication,
            @RequestBody WishlistItemRequest request) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Wishlist wishlist = wishlistRepository.findByUser(user).orElseGet(() -> {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setUser(user);
            return wishlistRepository.save(newWishlist);
        });

        Comic comic = comicRepository.findById(request.getComicId()).orElseThrow();
        wishlistItemRepository.findByWishlistAndComic(wishlist, comic).orElseGet(() -> {
            WishlistItem item = new WishlistItem();
            item.setWishlist(wishlist);
            item.setComic(comic);
            return wishlistItemRepository.save(item);
        });
        return getWishlist(authentication);
    }

    @DeleteMapping("/{comicId}")
    public ResponseEntity<List<WishlistItem>> remove(Authentication authentication, @PathVariable Long comicId) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Wishlist wishlist = wishlistRepository.findByUser(user).orElseThrow();
        Comic comic = comicRepository.findById(comicId).orElseThrow();
        wishlistItemRepository.findByWishlistAndComic(wishlist, comic).ifPresent(wishlistItemRepository::delete);
        return getWishlist(authentication);
    }
}
