package com.cogno.backend.timeline.dto;

import java.time.LocalDateTime;

public record TaskSegmentDto(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}