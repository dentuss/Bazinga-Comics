package com.bazinga.bazingabe.controller;

import com.bazinga.bazingabe.dto.NewsPostRequest;
import com.bazinga.bazingabe.dto.NewsPostResponse;
import com.bazinga.bazingabe.entity.NewsPost;
import com.bazinga.bazingabe.entity.User;
import com.bazinga.bazingabe.repository.NewsPostRepository;
import com.bazinga.bazingabe.repository.UserRepository;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsPostRepository newsPostRepository;
    private final UserRepository userRepository;

    public NewsController(NewsPostRepository newsPostRepository, UserRepository userRepository) {
        this.newsPostRepository = newsPostRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<NewsPostResponse>> getNews() {
        List<NewsPostResponse> responses = newsPostRepository
                .findByExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime.now())
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public ResponseEntity<NewsPostResponse> createNews(@Valid @RequestBody NewsPostRequest request,
            Authentication authentication) {
        User author = userRepository.findByEmail(authentication.getName()).orElseThrow();
        NewsPost post = new NewsPost();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(author);
        NewsPost saved = newsPostRepository.save(post);
        return ResponseEntity.ok(toResponse(saved));
    }

    private NewsPostResponse toResponse(NewsPost post) {
        User author = post.getAuthor();
        return new NewsPostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                author.getId(),
                author.getUsername(),
                author.getRole(),
                post.getCreatedAt(),
                post.getExpiresAt());
    }
}
