package com.bazinga.bazingabe.service;

import com.bazinga.bazingabe.repository.NewsPostRepository;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NewsCleanupService {

    private final NewsPostRepository newsPostRepository;

    public NewsCleanupService(NewsPostRepository newsPostRepository) {
        this.newsPostRepository = newsPostRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void purgeExpiredPosts() {
        newsPostRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
