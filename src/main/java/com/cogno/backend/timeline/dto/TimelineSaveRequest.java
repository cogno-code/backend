package com.cogno.backend.timeline.dto;

import java.time.LocalDate;
import java.util.List;

public record TimelineSaveRequest(
        LocalDate date,
        List<TaskDto> tasks,
        List<ChatEntryDto> entries
) {}