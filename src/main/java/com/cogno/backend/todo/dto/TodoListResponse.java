// src/main/java/com/cogno/backend/todo/dto/TodoListResponse.java
package com.cogno.backend.todo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoListResponse {
    private String date;                // ISO-8601 문자열
    private List<TodoItemDto> items;
}
