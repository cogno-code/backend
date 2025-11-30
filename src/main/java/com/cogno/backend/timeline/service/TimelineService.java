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

    /**
     * 특정 날짜 타임라인 전체를 덮어쓰기식으로 저장
     * - 기존 Task / ChatEntry 다 지우고 새로 저장
     */
    @Transactional
    public void saveTimeline(TimelineSaveRequest req) {
        LocalDate date = req.date();

        // 1) 기존 데이터 삭제
        chatEntryRepository.deleteByDate(date);
        taskRepository.deleteByDate(date);

        // 2) Task 저장
        List<Task> tasks = req.tasks().stream()
                .map(dto -> {
                    Task task = Task.builder()
                            .name(dto.name())
                            .color(dto.color())
                            .status(dto.status())
                            .date(date)
                            .build();

                    dto.segments().forEach(segDto -> {
                        TaskSegment seg = TaskSegment.builder()
                                .task(task)
                                .startTime(segDto.startTime())
                                .endTime(segDto.endTime())
                                .build();
                        task.getSegments().add(seg);
                    });

                    return task;
                })
                .toList();

        taskRepository.saveAll(tasks);

        // 3) ChatEntry 저장
        List<ChatEntry> entries = req.entries().stream()
                .map(dto -> ChatEntry.builder()
                        .date(date)
                        .createdAt(dto.createdAt())
                        .text(dto.text())
                        .type(dto.type())
                        .taskName(dto.taskName())
                        .systemKind(dto.systemKind())
                        .build()
                )
                .toList();

        chatEntryRepository.saveAll(entries);
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