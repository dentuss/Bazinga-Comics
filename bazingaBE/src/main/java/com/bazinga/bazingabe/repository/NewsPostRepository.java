package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.NewsPost;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {
    List<NewsPost> findByExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime now);
    long deleteByExpiresAtBefore(LocalDateTime now);
}
