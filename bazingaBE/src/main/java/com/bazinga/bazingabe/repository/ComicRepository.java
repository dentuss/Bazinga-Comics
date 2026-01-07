package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.Comic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComicRepository extends JpaRepository<Comic, Long> {
    List<Comic> findByRedactedFalse();
}
