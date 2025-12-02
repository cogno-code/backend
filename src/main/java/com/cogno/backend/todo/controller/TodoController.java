// src/main/java/com/cogno/backend/todo/controller/TodoController.java
package com.cogno.backend.todo.controller;

import com.cogno.backend.todo.dto.SaveTodosRequest;
import com.cogno.backend.todo.dto.TodoListResponse;
import com.cogno.backend.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        // 🔹 인증 안 된 경우 401
        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }

        // 🔹 /api/me 와 동일한 규칙으로 userKey 추출
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
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "date는 yyyy-MM-dd 형식이어야 합니다: " + dateStr,
                    e
            );
        }

        return todoService.getTodos(userKey, date);
    }

    @PutMapping
    public TodoListResponse saveTodos(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestBody SaveTodosRequest request
    ) {
        // 🔹 인증 안 된 경우 401
        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인이 필요합니다."
            );
        }

        String userKey = principal.getName();
        // 날짜 파싱/검증은 서비스에서 처리
        return todoService.replaceTodos(userKey, request);
    }
}
