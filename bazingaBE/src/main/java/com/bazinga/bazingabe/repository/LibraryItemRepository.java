package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.Library;
import com.bazinga.bazingabe.entity.LibraryItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {
    Optional<LibraryItem> findByLibraryAndComic(Library library, Comic comic);
    void deleteByComic(Comic comic);
}
