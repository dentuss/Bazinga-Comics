package com.bazinga.bazingabe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Mock
    private ComicRepository comicRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WishlistController wishlistController;

    @Test
    void getWishlistReturnsItemsForUser() {
        User user = createUser(8L, "wishlist@example.com");
        Wishlist wishlist = createWishlist(12L, user);
        Wishlist otherWishlist = createWishlist(13L, user);

        WishlistItem wishlistItem = createWishlistItem(51L, wishlist, 99L);
        WishlistItem otherItem = createWishlistItem(52L, otherWishlist, 199L);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), "password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));
        when(wishlistItemRepository.findAll()).thenReturn(List.of(wishlistItem, otherItem));

        var response = wishlistController.getWishlist(authentication);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(1, response.getBody().size());
        assertEquals(51L, response.getBody().getFirst().getId());
        assertEquals(99L, response.getBody().getFirst().getComic().getId());
    }

    @Test
    void addToWishlistCreatesItemWhenMissing() {
        User user = createUser(9L, "creator@example.com");
        Wishlist wishlist = createWishlist(21L, user);
        Comic comic = new Comic();
        comic.setId(77L);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), "password");

        WishlistItemRequest request = new WishlistItemRequest();
        request.setComicId(comic.getId());

        WishlistItem wishlistItem = createWishlistItem(61L, wishlist, comic.getId());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));
        when(comicRepository.findById(comic.getId())).thenReturn(Optional.of(comic));
        when(wishlistItemRepository.findByWishlistAndComic(wishlist, comic)).thenReturn(Optional.empty());
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenReturn(wishlistItem);
        when(wishlistItemRepository.findAll()).thenReturn(List.of(wishlistItem));

        var response = wishlistController.addToWishlist(authentication, request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(1, response.getBody().size());
        assertEquals(77L, response.getBody().getFirst().getComic().getId());
        verify(wishlistItemRepository).save(any(WishlistItem.class));
    }

    @Test
    void removeDeletesItemAndReturnsUpdatedWishlist() {
        User user = createUser(10L, "remover@example.com");
        Wishlist wishlist = createWishlist(31L, user);
        Comic comic = new Comic();
        comic.setId(88L);
        WishlistItem wishlistItem = createWishlistItem(71L, wishlist, comic.getId());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), "password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(wishlistRepository.findByUser(user)).thenReturn(Optional.of(wishlist));
        when(comicRepository.findById(comic.getId())).thenReturn(Optional.of(comic));
        when(wishlistItemRepository.findByWishlistAndComic(wishlist, comic)).thenReturn(Optional.of(wishlistItem));
        when(wishlistItemRepository.findAll()).thenReturn(List.of());

        var response = wishlistController.remove(authentication, comic.getId());

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(0, response.getBody().size());
        verify(wishlistItemRepository).findByWishlistAndComic(wishlist, comic);
    }

    private User createUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setUsername(email);
        return user;
    }

    private Wishlist createWishlist(Long id, User user) {
        Wishlist wishlist = new Wishlist();
        wishlist.setId(id);
        wishlist.setUser(user);
        return wishlist;
    }

    private WishlistItem createWishlistItem(Long id, Wishlist wishlist, Long comicId) {
        WishlistItem item = new WishlistItem();
        item.setId(id);
        item.setWishlist(wishlist);
        Comic comic = new Comic();
        comic.setId(comicId);
        item.setComic(comic);
        return item;
    }
}
