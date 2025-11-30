package com.cogno.backend.timeline.dto;

import com.cogno.backend.timeline.domain.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public record TaskDto(
        Long id,
        String name,
        String color,
        TaskStatus status,
        LocalDate date,
        List<TaskSegmentDto> segments
) {}