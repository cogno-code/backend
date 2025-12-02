// src/main/java/com/cogno/backend/todo/domain/TodoItem.java
package com.cogno.backend.todo.domain;

import com.cogno.backend.timeline.domain.TaskDefinition;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "todo_items",
        indexes = {
                @Index(name = "idx_todo_user_date", columnList = "user_key,target_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 유저의 Todo인지
    @Column(name = "user_key", nullable = false, length = 64)
    private String userKey;

    // 어떤 날짜의 Todo인지 (불렛저널 페이지 날짜)
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(nullable = false, length = 500)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TodoStatus status;

    // 선택된 TaskDefinition (없을 수도 있음)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_definition_id")
    private TaskDefinition taskDefinition;

    // 같은 날짜 + 같은 taskDefinition 안에서의 정렬 순서
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
