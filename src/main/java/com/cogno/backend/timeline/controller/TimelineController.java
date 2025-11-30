package com.cogno.backend.timeline.controller;

import com.cogno.backend.timeline.domain.TaskDefinition;
import com.cogno.backend.timeline.dto.*;
import com.cogno.backend.timeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    /**
     * GET /api/timeline?date=2025-11-30
     */
    @GetMapping
    public TimelineResponse getTimeline(@RequestParam("date") LocalDate date) {
        return timelineService.getTimeline(date);
    }

    /**
     * GET /api/timeline/task-definitions
     * - 카테고리 목록 (프론트 TaskDefinition 초기값/관리용)
     */
    @GetMapping("/task-definitions")
    public List<TaskDefinition> getTaskDefinitions() {
        return timelineService.getTaskDefinitions();
    }

    /**
     * POST /api/timeline/task-definitions
     * - 카테고리 추가
     */
    @PostMapping("/task-definitions")
    public TaskDefinition createTaskDefinition(
            @RequestParam String name,
            @RequestParam String color
    ) {
        return timelineService.createTaskDefinition(name, color);
    }

    @PostMapping("/chat")
    public ChatEntryDto createChatEntry(@RequestBody ChatCreateRequest req) {
        return timelineService.createChatEntry(req);
    }

    // ✅ 채팅 수정
    @PatchMapping("/chat/{id}")
    public ChatEntryDto updateChatEntry(
            @PathVariable Long id,
            @RequestBody ChatUpdateRequest req
    ) {
        return timelineService.updateChatEntry(id, req);
    }

    // ✅ 채팅 삭제
    @DeleteMapping("/chat/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChatEntry(@PathVariable Long id) {
        timelineService.deleteChatEntry(id);
    }
}