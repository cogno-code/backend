package com.cogno.backend.timeline.repository;

import com.cogno.backend.timeline.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDate(LocalDate date);
    void deleteByDate(LocalDate date);
}