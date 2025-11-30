package com.cogno.backend.timeline.repository;

import com.cogno.backend.timeline.domain.ChatEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ChatEntryRepository extends JpaRepository<ChatEntry, Long> {

    // 특정 유저 + 날짜의 채팅을 시간 순으로
    List<ChatEntry> findByUserKeyAndDateOrderByCreatedAtAsc(String userKey, LocalDate date);

    // 특정 유저 + 날짜 기준으로 삭제
    void deleteByUserKeyAndDate(String userKey, LocalDate date);
}
