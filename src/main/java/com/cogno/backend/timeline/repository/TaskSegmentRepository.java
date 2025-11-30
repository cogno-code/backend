package com.cogno.backend.timeline.repository;

import com.cogno.backend.timeline.domain.TaskSegment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskSegmentRepository extends JpaRepository<TaskSegment, Long> {
}