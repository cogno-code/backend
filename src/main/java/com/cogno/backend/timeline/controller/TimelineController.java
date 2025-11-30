package com.cogno.backend.timeline.controller;

import com.cogno.backend.timeline.domain.TaskDefinition;
import com.cogno.backend.timeline.dto.*;
import com.cogno.backend.timeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"  // React에서 쿠키 쓸 거라 켜둠
)
public class TimelineController {

    private final TimelineService timelineService;

    /**
     * 공통: OAuth2User에서 userKey 뽑기
     */
    private String getUserKey(OAuth2User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Long kakaoId = user.getAttribute("id");
        if (kakaoId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "카카오 사용자 ID를 찾을 수 없습니다.");
        }
        return String.valueOf(kakaoId);
    }

    /**
     * GET /api/timeline?date=2025-11-30
     * - 현재 로그인한 유저의 해당 날짜 타임라인
     */
    @GetMapping
    public TimelineResponse getTimeline(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal OAuth2User user
    ) {
        String userKey = getUserKey(user);
        return timelineService.getTimeline(userKey, date);
    }

    /**
     * GET /api/timeline/task-definitions
     * - 현재 유저의 카테고리(TaskDefinition) 목록
     */
    @GetMapping("/task-definitions")
    public List<TaskDefinition> getTaskDefinitions(
            @AuthenticationPrincipal OAuth2User user
    ) {
        String userKey = getUserKey(user);
        return timelineService.getTaskDefinitions(userKey);
    }

    /**
     * POST /api/timeline/task-definitions
     * - 현재 유저의 카테고리 추가
     */
    @PostMapping("/task-definitions")
    public TaskDefinition createTaskDefinition(
            @RequestParam String name,
            @RequestParam String color,
            @AuthenticationPrincipal OAuth2User user
    ) {
        String userKey = getUserKey(user);
        return timelineService.createTaskDefinition(userKey, name, color);
    }

    /**
     * POST /api/timeline/chat
     * - 현재 유저의 채팅 추가
     */
    @PostMapping("/chat")
    public ChatEntryDto createChatEntry(
            @RequestBody ChatCreateRequest req,
            @AuthenticationPrincipal OAuth2User user
    ) {
        String userKey = getUserKey(user);
        return timelineService.createChatEntry(userKey, req);
    }

    /**
     * PATCH /api/timeline/chat/{id}
     * - 현재 유저의 채팅 수정 (본인 것만)
     */
    @PatchMapping("/chat/{id}")
    public ChatEntryDto updateChatEntry(
            @PathVariable Long id,
            @RequestBody ChatUpdateRequest req,
            @AuthenticationPrincipal OAuth2User user
    ) {
        String userKey = getUserKey(user);
        return timelineService.updateChatEntry(userKey, id, req);
    }

    /**
     * DELETE /api/timeline/chat/{id}
     * - 현재 유저의 채팅 삭제 (본인 것만)
     */
    @DeleteMapping("/chat/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChatEntry(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User user
    ) {
        String userKey = getUserKey(user);
        timelineService.deleteChatEntry(userKey, id);
    }
}
