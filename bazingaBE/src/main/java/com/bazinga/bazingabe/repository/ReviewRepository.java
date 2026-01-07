package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    void deleteByComic(Comic comic);
}
