// src/main/java/com/cogno/backend/todo/dto/SaveTodosRequest.java
package com.cogno.backend.todo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveTodosRequest {

    // "2025-12-02" 같은 ISO-8601 날짜 문자열
    private String date;

    private List<TodoItemDto> items;
}
