package com.cogno.backend.timeline.controller;

import com.cogno.backend.timeline.domain.TaskDefinition;
import com.cogno.backend.timeline.dto.TimelineResponse;
import com.cogno.backend.timeline.dto.TimelineSaveRequest;
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
     * POST /api/timeline
     * body: TimelineSaveRequest
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveTimeline(@RequestBody TimelineSaveRequest request) {
        timelineService.saveTimeline(request);
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
}