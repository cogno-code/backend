package com.cogno.backend.timeline.service;

import com.cogno.backend.timeline.domain.ChatEntry;
import com.cogno.backend.timeline.domain.Task;
import com.cogno.backend.timeline.domain.TaskDefinition;
import com.cogno.backend.timeline.domain.TaskSegment;
import com.cogno.backend.timeline.domain.TaskStatus;
import com.cogno.backend.timeline.dto.ChatCreateRequest;
import com.cogno.backend.timeline.dto.ChatEntryDto;
import com.cogno.backend.timeline.dto.ChatUpdateRequest;
import com.cogno.backend.timeline.dto.TaskDto;
import com.cogno.backend.timeline.dto.TaskSegmentDto;
import com.cogno.backend.timeline.dto.TimelineResponse;
import com.cogno.backend.timeline.repository.ChatEntryRepository;
import com.cogno.backend.timeline.repository.TaskDefinitionRepository;
import com.cogno.backend.timeline.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final TaskRepository taskRepository;
    private final ChatEntryRepository chatEntryRepository;
    private final TaskDefinitionRepository taskDefinitionRepository;

    // ====== 🔹 TASK_START 처리 ======
    private void handleTaskStart(String userKey, ChatCreateRequest req, LocalDateTime now) {
        String taskName = req.taskName();
        if (taskName == null || taskName.isBlank()) {
            return;
        }
        LocalDate date = req.date();

        // user + date + name 으로 Task 찾기
        Task task = taskRepository.findByUserKeyAndDateAndName(userKey, date, taskName)
                .orElse(null);

        if (task == null) {
            // 없으면 새 Task 생성 (색은 TaskDefinition에서 가져오고, 없으면 기본값)
            String color = "#22c55e"; // default
            Optional<TaskDefinition> defOpt =
                    taskDefinitionRepository.findByUserKeyAndName(userKey, taskName);

            if (defOpt.isPresent()) {
                color = defOpt.get().getColor();
            }

            task = Task.builder()
                    .userKey(userKey)
                    .date(date)
                    .name(taskName)
                    .color(color)
                    .status(TaskStatus.RUNNING)
                    .build();
        } else {
            // 이미 존재하면 다시 RUNNING 상태로
            task.setStatus(TaskStatus.RUNNING);
        }

        // 새 Segment 추가 (startTime = now, endTime = null)
        TaskSegment segment = TaskSegment.builder()
                .task(task)
                .startTime(now)
                .endTime(null)
                .build();
        task.getSegments().add(segment);

        taskRepository.save(task);
    }

    // ====== 🔹 TASK_END 처리 ======
    private void handleTaskEnd(String userKey, ChatCreateRequest req, LocalDateTime now) {
        String taskName = req.taskName();
        if (taskName == null || taskName.isBlank()) {
            return;
        }
        LocalDate date = req.date();

        Task task = taskRepository.findByUserKeyAndDateAndName(userKey, date, taskName)
                .orElse(null);
        if (task == null) {
            // 열려 있는 task가 없으면 그냥 무시
            return;
        }

        // 가장 마지막 segment 중 endTime 이 null 인 걸 닫기
        List<TaskSegment> segments = task.getSegments();
        if (segments.isEmpty()) {
            return;
        }

        TaskSegment last = segments.get(segments.size() - 1);
        if (last.getEndTime() == null) {
            last.setEndTime(now);
            task.setStatus(TaskStatus.FINISHED);
            taskRepository.save(task);
        }
    }

    /**
     * ✅ 유저별 + 날짜별 타임라인 조회
     */
    @Transactional
    public TimelineResponse getTimeline(String userKey, LocalDate date) {
        // 유저 + 날짜 기준으로 조회
        List<Task> tasks =
                taskRepository.findByUserKeyAndDateOrderByIdAsc(userKey, date);
        List<ChatEntry> entries =
                chatEntryRepository.findByUserKeyAndDateOrderByCreatedAtAsc(userKey, date);

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
     * ✅ 채팅 생성 (유저별)
     */
    @Transactional
    public ChatEntryDto createChatEntry(String userKey, ChatCreateRequest req) {
        // 서버 기준 시간(Asia/Seoul) 사용
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        ChatEntry entry = ChatEntry.builder()
                .userKey(userKey)
                .date(req.date())
                .createdAt(now)
                .text(req.text())
                .type(req.type())
                .taskName(req.taskName())
                .systemKind(req.systemKind())
                .build();

        ChatEntry saved = chatEntryRepository.save(entry);

        // systemKind에 따라 Task/Segment 업데이트
        if (req.systemKind() != null) {
            switch (req.systemKind()) {
                case TASK_START -> handleTaskStart(userKey, req, now);
                case TASK_END -> handleTaskEnd(userKey, req, now);
                default -> {
                    // INFO 등은 무시
                }
            }
        }

        return new ChatEntryDto(
                saved.getId(),
                saved.getCreatedAt(),
                saved.getText(),
                saved.getType(),
                saved.getTaskName(),
                saved.getSystemKind()
        );
    }

    /**
     * ✅ 채팅 수정 (본인 것만)
     */
    @Transactional
    public ChatEntryDto updateChatEntry(String userKey, Long id, ChatUpdateRequest req) {
        ChatEntry entry = chatEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ChatEntry not found: " + id));

        // 내 것이 아니면 막기
        if (!entry.getUserKey().equals(userKey)) {
            throw new AccessDeniedException("다른 사용자의 채팅은 수정할 수 없습니다.");
        }

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

    /**
     * ✅ 채팅 삭제 (본인 것만)
     */
    @Transactional
    public void deleteChatEntry(String userKey, Long id) {
        ChatEntry entry = chatEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ChatEntry not found: " + id));

        if (!entry.getUserKey().equals(userKey)) {
            throw new AccessDeniedException("다른 사용자의 채팅은 삭제할 수 없습니다.");
        }

        chatEntryRepository.delete(entry);
    }

    /**
     * ✅ TaskDefinition 목록 (유저별 프리셋)
     */
    public List<TaskDefinition> getTaskDefinitions(String userKey) {
        return taskDefinitionRepository.findByUserKeyOrderByIdAsc(userKey);
    }

    /**
     * ✅ TaskDefinition 생성 (유저별)
     */
    @Transactional
    public TaskDefinition createTaskDefinition(String userKey, String name, String color) {
        // 한 유저 안에서는 같은 이름 중복 방지
        taskDefinitionRepository.findByUserKeyAndName(userKey, name)
                .ifPresent(def -> {
                    throw new IllegalArgumentException("이미 존재하는 Task 이름입니다: " + name);
                });

        TaskDefinition def = TaskDefinition.builder()
                .userKey(userKey)
                .name(name)
                .color(color)
                .build();
        return taskDefinitionRepository.save(def);
    }
}
