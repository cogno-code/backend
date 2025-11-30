package com.cogno.backend.timeline.repository;

import com.cogno.backend.timeline.domain.TaskDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long> {
    Optional<TaskDefinition> findByName(String name);
}