// src/main/java/com/cogno/backend/todo/dto/TodoItemDto.java
package com.cogno.backend.todo.dto;

import com.cogno.backend.todo.domain.TodoStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItemDto {
    private Long id;
    private String text;
    private TodoStatus status;      // "TODO", "DONE", ...
    private Long taskDefinitionId;  // nullable
    private Integer order;          // 정렬 순서
}
