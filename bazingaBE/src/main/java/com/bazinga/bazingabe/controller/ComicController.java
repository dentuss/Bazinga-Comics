package com.bazinga.bazingabe.controller;

import com.bazinga.bazingabe.dto.ComicCreateRequest;
import com.bazinga.bazingabe.entity.Category;
import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.ComicCondition;
import com.bazinga.bazingabe.repository.CategoryRepository;
import com.bazinga.bazingabe.repository.ComicConditionRepository;
import com.bazinga.bazingabe.repository.ComicRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comics")
public class ComicController {

    private final ComicRepository comicRepository;
    private final CategoryRepository categoryRepository;
    private final ComicConditionRepository comicConditionRepository;

    public ComicController(ComicRepository comicRepository, CategoryRepository categoryRepository,
                ComicConditionRepository comicConditionRepository) {
            this.comicRepository = comicRepository;
            this.categoryRepository = categoryRepository;
            this.comicConditionRepository = comicConditionRepository;
        }

        @GetMapping
        public List<Comic> getAll() {
            return comicRepository.findAll();
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @PreAuthorize("hasRole('ADMIN')")
        public Comic create(@RequestBody ComicCreateRequest request) {
            Comic comic = new Comic();
            comic.setTitle(request.getTitle());
            comic.setAuthor(request.getAuthor());
            comic.setIsbn(request.getIsbn());
            comic.setDescription(request.getDescription());
            comic.setPublishedYear(request.getPublishedYear());
            comic.setPrice(request.getPrice());
            comic.setImage(request.getImage());

            if (request.getCategoryId() != null) {
                Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();
                comic.setCategory(category);
            }

            if (request.getConditionId() != null) {
                ComicCondition condition = comicConditionRepository.findById(request.getConditionId()).orElseThrow();
                comic.setCondition(condition);
            }

            return comicRepository.save(comic);
        }
    }