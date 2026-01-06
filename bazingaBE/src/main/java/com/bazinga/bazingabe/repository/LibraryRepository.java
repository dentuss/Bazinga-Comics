package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.Library;
import com.bazinga.bazingabe.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library, Long> {
    Optional<Library> findByUser(User user);
}
