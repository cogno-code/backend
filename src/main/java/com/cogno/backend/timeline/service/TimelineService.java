package com.cogno.backend.timeline.service;

import com.cogno.backend.timeline.domain.ChatEntry;
import com.cogno.backend.timeline.domain.Task;
import com.cogno.backend.timeline.domain.TaskDefinition;
import com.cogno.backend.timeline.domain.TaskSegment;
import com.cogno.backend.timeline.dto.*;
import com.cogno.backend.timeline.repository.ChatEntryRepository;
import com.cogno.backend.timeline.repository.TaskDefinitionRepository;
import com.cogno.backend.timeline.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final TaskRepository taskRepository;
    private final ChatEntryRepository chatEntryRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;

    @Transactional
    public TimelineResponse getTimeline(LocalDate date) {
        List<Task> tasks = taskRepository.findByDate(date);
        List<ChatEntry> entries = chatEntryRepository.findByDateOrderByCreatedAtAsc(date);

        List<TaskDto> taskDtos = tasks.stream()
                .map(t -> new TaskDto(
                        t.getId(),
                        t.getName(),
                        t.getColor(),
                        t.getStatus(),
                        t.getDate(),
                        t.getSegments().stream()
                                .map(s -> new TaskSegmentDto(
                                        s.getId(),
                                        s.getStartTime(),
                                        s.getEndTime()
                                ))
                                .toList()
                ))
                .toList();

        List<ChatEntryDto> entryDtos = entries.stream()
                .map(e -> new ChatEntryDto(
                        e.getId(),
                        e.getCreatedAt(),
                        e.getText(),
                        e.getType(),
                        e.getTaskName(),
                        e.getSystemKind()
                ))
                .toList();

        return new TimelineResponse(date, taskDtos, entryDtos);
    }

    @Transactional
    public ChatEntryDto createChatEntry(ChatCreateRequest req) {
        // 서버 기준 시간(Asia/Seoul) 사용
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        ChatEntry entry = ChatEntry.builder()
                .date(req.date())
                .createdAt(now)
                .text(req.text())
                .type(req.type())
                .taskName(req.taskName())
                .systemKind(req.systemKind())
                .build();

        ChatEntry saved = chatEntryRepository.save(entry);

        return new ChatEntryDto(
                saved.getId(),
                saved.getCreatedAt(),
                saved.getText(),
                saved.getType(),
                saved.getTaskName(),
                saved.getSystemKind()
        );
    }

    // ✅ 채팅 수정
    @Transactional
    public ChatEntryDto updateChatEntry(Long id, ChatUpdateRequest req) {
        ChatEntry entry = chatEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ChatEntry not found: " + id));

        entry.setText(req.text());

        ChatEntry saved = chatEntryRepository.save(entry);

        return new ChatEntryDto(
                saved.getId(),
                saved.getCreatedAt(),
                saved.getText(),
                saved.getType(),
                saved.getTaskName(),
                saved.getSystemKind()
        );
    }

    // ✅ 채팅 삭제
    @Transactional
    public void deleteChatEntry(Long id) {
        chatEntryRepository.deleteById(id);
    }

    /**
     * TaskDefinition 관리용 (카테고리 목록)
     */
    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitionRepository.findAll();
    }

    @Transactional
    public TaskDefinition createTaskDefinition(String name, String color) {
        TaskDefinition def = TaskDefinition.builder()
                .name(name)
                .color(color)
                .build();
        return taskDefinitionRepository.save(def);
    }


}