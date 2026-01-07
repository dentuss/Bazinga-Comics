package com.bazinga.bazingabe.repository;

import com.bazinga.bazingabe.entity.Comic;
import com.bazinga.bazingabe.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    void deleteByComic(Comic comic);
}
