package com.cogno.backend.timeline.repository;

import com.cogno.backend.timeline.domain.ChatEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ChatEntryRepository extends JpaRepository<ChatEntry, Long> {
    List<ChatEntry> findByDateOrderByCreatedAtAsc(LocalDate date);
    void deleteByDate(LocalDate date);
}