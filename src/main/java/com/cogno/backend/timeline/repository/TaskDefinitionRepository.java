package com.cogno.backend.timeline.repository;

import com.cogno.backend.timeline.domain.TaskDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, Long> {

    // 특정 유저의 전체 TaskDefinition 목록
    List<TaskDefinition> findByUserKeyOrderByIdAsc(String userKey);

    // 특정 유저 + 이름으로 조회 (중복 방지용)
    Optional<TaskDefinition> findByUserKeyAndName(String userKey, String name);
}
