package com.bazinga.bazingabe.controller;

import com.bazinga.bazingabe.entity.ComicCondition;
import com.bazinga.bazingabe.repository.ComicConditionRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conditions")
public class ComicConditionController {

    private final ComicConditionRepository comicConditionRepository;

    public ComicConditionController(ComicConditionRepository comicConditionRepository) {
        this.comicConditionRepository = comicConditionRepository;
    }

    @GetMapping
    public List<ComicCondition> getAll() {
        return comicConditionRepository.findAll();
    }
}