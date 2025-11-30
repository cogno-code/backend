package com.cogno.backend.timeline.dto;

import com.cogno.backend.timeline.domain.ChatType;
import com.cogno.backend.timeline.domain.SystemKind;

import java.time.LocalDateTime;

public record ChatEntryDto(
        Long id,
        LocalDateTime createdAt,
        String text,
        ChatType type,
        String taskName,
        SystemKind systemKind
) {}