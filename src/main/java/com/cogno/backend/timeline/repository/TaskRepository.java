package com.cogno.backend.timeline.repository;

import com.cogno.backend.timeline.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // 특정 유저 + 날짜의 Task 목록
    List<Task> findByUserKeyAndDateOrderByIdAsc(String userKey, LocalDate date);

    // 특정 유저 + 날짜의 Task 전체 삭제
    void deleteByUserKeyAndDate(String userKey, LocalDate date);

    Optional<Task> findByUserKeyAndDateAndName(String userKey, LocalDate date, String name);
}
