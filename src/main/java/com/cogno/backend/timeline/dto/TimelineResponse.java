package com.cogno.backend.timeline.dto;

import java.util.List;
import java.time.LocalDate;

public record TimelineResponse(
        LocalDate date,
        List<TaskDto> tasks,
        List<ChatEntryDto> entries
) {}