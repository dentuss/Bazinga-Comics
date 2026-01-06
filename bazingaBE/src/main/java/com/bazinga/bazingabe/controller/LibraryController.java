package com.bazinga.bazingabe.controller;

import com.bazinga.bazingabe.dto.LibraryItemRequest;
import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.Library;
import com.bazinga.bazingabe.entity.LibraryItem;
import com.bazinga.bazingabe.entity.User;
import com.bazinga.bazingabe.repository.ComicRepository;
import com.bazinga.bazingabe.repository.LibraryItemRepository;
import com.bazinga.bazingabe.repository.LibraryRepository;
import com.bazinga.bazingabe.repository.UserRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryRepository libraryRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final ComicRepository comicRepository;
    private final UserRepository userRepository;

    public LibraryController(LibraryRepository libraryRepository, LibraryItemRepository libraryItemRepository,
            ComicRepository comicRepository, UserRepository userRepository) {
        this.libraryRepository = libraryRepository;
        this.libraryItemRepository = libraryItemRepository;
        this.comicRepository = comicRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<LibraryItem>> getLibrary(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Library library = libraryRepository.findByUser(user).orElseGet(() -> {
            Library newLibrary = new Library();
            newLibrary.setUser(user);
            return libraryRepository.save(newLibrary);
        });
        return ResponseEntity.ok(libraryItemRepository.findAll().stream()
                .filter(item -> item.getLibrary().getId().equals(library.getId()))
                .toList());
    }

    @PostMapping
    public ResponseEntity<List<LibraryItem>> addToLibrary(Authentication authentication,
            @RequestBody LibraryItemRequest request) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Library library = libraryRepository.findByUser(user).orElseGet(() -> {
            Library newLibrary = new Library();
            newLibrary.setUser(user);
            return libraryRepository.save(newLibrary);
        });

        Comic comic = comicRepository.findById(request.getComicId()).orElseThrow();
        libraryItemRepository.findByLibraryAndComic(library, comic).orElseGet(() -> {
            LibraryItem item = new LibraryItem();
            item.setLibrary(library);
            item.setComic(comic);
            return libraryItemRepository.save(item);
        });
        return getLibrary(authentication);
    }
}
