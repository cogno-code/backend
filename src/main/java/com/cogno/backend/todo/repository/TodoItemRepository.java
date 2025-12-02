// src/main/java/com/cogno/backend/todo/repository/TodoItemRepository.java
package com.cogno.backend.todo.repository;

import com.cogno.backend.todo.domain.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    List<TodoItem> findByUserKeyAndTargetDateOrderBySortOrderAsc(
            String userKey,
            LocalDate targetDate
    );

    void deleteByUserKeyAndTargetDate(String userKey, LocalDate targetDate);
}
