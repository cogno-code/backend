// src/main/java/com/cogno/backend/timeline/dto/ChatCreateRequest.java
package com.cogno.backend.timeline.dto;

import com.cogno.backend.timeline.domain.ChatType;
import com.cogno.backend.timeline.domain.SystemKind;

import java.time.LocalDate;

public record ChatCreateRequest(
        LocalDate date,
        String text,
        ChatType type,
        String taskName,
        SystemKind systemKind
) {}
