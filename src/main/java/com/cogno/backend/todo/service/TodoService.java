// src/main/java/com/cogno/backend/todo/service/TodoService.java
package com.cogno.backend.todo.service;

import com.cogno.backend.timeline.domain.TaskDefinition;
import com.cogno.backend.timeline.repository.TaskDefinitionRepository;
import com.cogno.backend.todo.domain.TodoItem;
import com.cogno.backend.todo.domain.TodoStatus;
import com.cogno.backend.todo.dto.SaveTodosRequest;
import com.cogno.backend.todo.dto.TodoItemDto;
import com.cogno.backend.todo.dto.TodoListResponse;
import com.cogno.backend.todo.repository.TodoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoItemRepository todoItemRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;

    @Transactional(readOnly = true)
    public TodoListResponse getTodos(String userKey, LocalDate date) {
        List<TodoItem> items =
                todoItemRepository.findByUserKeyAndTargetDateOrderBySortOrderAsc(userKey, date);

        List<TodoItemDto> dtoList = items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return TodoListResponse.builder()
                .date(date.toString())
                .items(dtoList)
                .build();
    }

    @Transactional
    public TodoListResponse replaceTodos(String userKey, SaveTodosRequest request) {
        // 🔹 date 필드 검증 & 파싱
        if (request.getDate() == null || request.getDate().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "date는 필수입니다."
            );
        }

        LocalDate date;
        try {
            date = LocalDate.parse(request.getDate());
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "date는 yyyy-MM-dd 형식이어야 합니다: " + request.getDate(),
                    e
            );
        }

        // 1) 기존 기록 삭제
        todoItemRepository.deleteByUserKeyAndTargetDate(userKey, date);

        // 2) TaskDefinition 미리 캐시
        Map<Long, TaskDefinition> taskDefCache = new HashMap<>();
        for (TodoItemDto dto : Optional.ofNullable(request.getItems()).orElse(List.of())) {
            Long taskDefId = dto.getTaskDefinitionId();
            if (taskDefId != null && !taskDefCache.containsKey(taskDefId)) {
                taskDefinitionRepository.findById(taskDefId)
                        .ifPresent(td -> taskDefCache.put(taskDefId, td));
            }
        }

        // 3) 새로 저장
        List<TodoItem> toSave = new ArrayList<>();
        List<TodoItemDto> items = Optional.ofNullable(request.getItems()).orElse(List.of());

        for (int i = 0; i < items.size(); i++) {
            TodoItemDto dto = items.get(i);

            TaskDefinition taskDefinition = null;
            if (dto.getTaskDefinitionId() != null) {
                taskDefinition = taskDefCache.get(dto.getTaskDefinitionId());
            }

            TodoStatus status = dto.getStatus() != null ? dto.getStatus() : TodoStatus.TODO;

            TodoItem entity = TodoItem.builder()
                    .userKey(userKey)
                    .targetDate(date)
                    .text(dto.getText() != null ? dto.getText() : "")
                    .status(status)
                    .taskDefinition(taskDefinition)
                    .sortOrder(dto.getOrder() != null ? dto.getOrder() : i)
                    .build();

            toSave.add(entity);
        }

        List<TodoItem> saved = todoItemRepository.saveAll(toSave);

        List<TodoItemDto> resultDtos = saved.stream()
                .sorted(Comparator.comparing(TodoItem::getSortOrder))
                .map(this::toDto)
                .collect(Collectors.toList());

        return TodoListResponse.builder()
                .date(date.toString())
                .items(resultDtos)
                .build();
    }

    private TodoItemDto toDto(TodoItem item) {
        Long taskDefId = item.getTaskDefinition() != null
                ? item.getTaskDefinition().getId()
                : null;

        return TodoItemDto.builder()
                .id(item.getId())
                .text(item.getText())
                .status(item.getStatus())
                .taskDefinitionId(taskDefId)
                .order(item.getSortOrder())
                .build();
    }
}
