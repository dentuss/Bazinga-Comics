package com.bazinga.bazingabe.controller;

import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.repository.ComicRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comics")
public class ComicController {

    private final ComicRepository comicRepository;

    public ComicController(ComicRepository comicRepository) {
        this.comicRepository = comicRepository;
    }

    @GetMapping
    public List<Comic> getAll() {
        return comicRepository.findAll();
    }
}
