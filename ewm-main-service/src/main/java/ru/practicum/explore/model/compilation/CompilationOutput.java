package ru.practicum.explore.model.compilation;

import lombok.Data;
import ru.practicum.explore.model.event.EventOutput;

import java.util.List;

@Data
public class CompilationOutput {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventOutput> events;
}
