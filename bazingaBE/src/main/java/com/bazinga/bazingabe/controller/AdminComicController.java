package com.bazinga.bazingabe.controller;

import com.bazinga.bazingabe.dto.ComicRedactionRequest;
import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.repository.ComicRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    public AdminComicController(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
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
}
