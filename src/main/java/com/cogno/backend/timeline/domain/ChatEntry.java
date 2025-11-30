package com.cogno.backend.timeline.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_entries",
        indexes = {
                @Index(name = "idx_chat_user_date", columnList = "user_key, date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어느 유저의 타임라인인지 (카카오 id 기반 문자열 등)
     */
    @Column(name = "user_key", nullable = false, length = 64)
    private String userKey;

    /**
     * 어느 날짜의 타임라인인지
     */
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatType type;

    // UI에서 쓰는 taskName (null일 수 있음)
    @Column(length = 100)
    private String taskName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SystemKind systemKind;
}
