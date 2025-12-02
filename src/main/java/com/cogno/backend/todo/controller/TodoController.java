// src/main/java/com/cogno/backend/todo/controller/TodoController.java
package com.cogno.backend.todo.controller;

import com.cogno.backend.todo.dto.SaveTodosRequest;
import com.cogno.backend.todo.dto.TodoListResponse;
import com.cogno.backend.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public TodoListResponse getTodos(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam(value = "date", required = false) String dateStr
    ) {
        // 🔹 /api/me 에서 userKey 뽑는 규칙이랑 똑같이 맞춰주면 됨
        String userKey = principal.getName(); // 필요하면 email 등으로 변경

        LocalDate date;
        try {
            if (dateStr == null || dateStr.isBlank()) {
                date = LocalDate.now();
            } else {
                // 프론트에서 "YYYY-MM-DD"로 보내니까 이렇게 파싱
                date = LocalDate.parse(dateStr);
            }
        } catch (DateTimeParseException e) {
            // 잘못된 형식인 경우만 400
            throw new IllegalArgumentException("date는 yyyy-MM-dd 형식이어야 합니다: " + dateStr);
        }

        return todoService.getTodos(userKey, date);
    }

    @PutMapping
    public TodoListResponse saveTodos(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestBody SaveTodosRequest request
    ) {
        String userKey = principal.getName();
        return todoService.replaceTodos(userKey, request);
    }
}
