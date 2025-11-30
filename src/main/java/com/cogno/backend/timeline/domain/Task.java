package com.cogno.backend.timeline.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 날짜의 타임라인인지 (일단 날짜 단위로 저장)
    @Column(nullable = false)
    private LocalDate date;

    // 프론트에서 쓰는 이름 (TaskDefinition.name과 동일하게 맞춰서 사용)
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 16)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskSegment> segments = new ArrayList<>();
}