package com.bazinga.bazingabe.controller;

import com.bazinga.bazingabe.dto.ComicCreateRequest;
import com.bazinga.bazingabe.dto.ComicRedactionRequest;
import com.bazinga.bazingabe.entity.Category;
import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.ComicCondition;
import com.bazinga.bazingabe.entity.ComicType;
import com.bazinga.bazingabe.repository.CartItemRepository;
import com.bazinga.bazingabe.repository.CategoryRepository;
import com.bazinga.bazingabe.repository.ComicConditionRepository;
import com.bazinga.bazingabe.repository.ComicRepository;
import com.bazinga.bazingabe.repository.LibraryItemRepository;
import com.bazinga.bazingabe.repository.ReportRepository;
import com.bazinga.bazingabe.repository.ReviewRepository;
import com.bazinga.bazingabe.repository.WishlistItemRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/comics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminComicController {

    private final ComicRepository comicRepository;
    private final CategoryRepository categoryRepository;
    private final ComicConditionRepository comicConditionRepository;
    private final CartItemRepository cartItemRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final ReviewRepository reviewRepository;
    private final ReportRepository reportRepository;

    public AdminComicController(
            ComicRepository comicRepository,
            CategoryRepository categoryRepository,
            ComicConditionRepository comicConditionRepository,
            CartItemRepository cartItemRepository,
            WishlistItemRepository wishlistItemRepository,
            LibraryItemRepository libraryItemRepository,
            ReviewRepository reviewRepository,
            ReportRepository reportRepository) {
        this.comicRepository = comicRepository;
        this.categoryRepository = categoryRepository;
        this.comicConditionRepository = comicConditionRepository;
        this.cartItemRepository = cartItemRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.libraryItemRepository = libraryItemRepository;
        this.reviewRepository = reviewRepository;
        this.reportRepository = reportRepository;
    }

    @GetMapping
    public ResponseEntity<List<Comic>> getComics() {
        List<Comic> comics = comicRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(comics);
    }

    @PutMapping("/{id}/redaction")
    public ResponseEntity<Comic> updateRedaction(
            @PathVariable Long id,
            @RequestBody ComicRedactionRequest request) {
        Comic comic = comicRepository.findById(id).orElse(null);
        if (comic == null) {
            return ResponseEntity.notFound().build();
        }

        comic.setRedacted(request.isRedacted());
        Comic saved = comicRepository.save(comic);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comic> updateComic(
            @PathVariable Long id,
            @RequestBody ComicCreateRequest request) {
        Comic comic = comicRepository.findById(id).orElse(null);
        if (comic == null) {
            return ResponseEntity.notFound().build();
        }

        comic.setTitle(request.getTitle());
        comic.setAuthor(request.getAuthor());
        comic.setIsbn(request.getIsbn());
        comic.setDescription(request.getDescription());
        comic.setMainCharacter(request.getMainCharacter());
        comic.setSeries(request.getSeries());
        comic.setPublishedYear(request.getPublishedYear());
        comic.setPrice(request.getPrice());
        comic.setImage(request.getImage());
        comic.setComicType(ComicType.fromValue(request.getComicType()));

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();
            comic.setCategory(category);
        } else {
            comic.setCategory(null);
        }

        if (request.getConditionId() != null) {
            ComicCondition condition = comicConditionRepository.findById(request.getConditionId()).orElseThrow();
            comic.setCondition(condition);
        } else {
            comic.setCondition(null);
        }

        Comic saved = comicRepository.save(comic);
        return ResponseEntity.ok(saved);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComic(@PathVariable Long id) {
        Comic comic = comicRepository.findById(id).orElse(null);
        if (comic == null) {
            return ResponseEntity.notFound().build();
        }

        if (!comic.isRedacted()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        cartItemRepository.deleteByComic(comic);
        wishlistItemRepository.deleteByComic(comic);
        libraryItemRepository.deleteByComic(comic);
        reviewRepository.deleteByComic(comic);
        reportRepository.deleteByComic(comic);
        comicRepository.delete(comic);
        return ResponseEntity.noContent().build();
    }
}
