package com.cogno.backend.timeline.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_definitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // "#22c55e" 이런 형식
    @Column(nullable = false, length = 16)
    private String color;
}