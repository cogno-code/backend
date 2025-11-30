package com.cogno.backend.timeline.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "task_definitions",
        indexes = {
                @Index(name = "idx_taskdef_user", columnList = "user_key")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어느 유저의 프리셋인지
     * - 공용 프리셋으로 쓸 거면 이 필드를 삭제해도 됨
     */
    @Column(name = "user_key", nullable = false, length = 64)
    private String userKey;

    @Column(nullable = false, length = 100)
    private String name;

    // "#22c55e" 이런 형식
    @Column(nullable = false, length = 16)
    private String color;
}
