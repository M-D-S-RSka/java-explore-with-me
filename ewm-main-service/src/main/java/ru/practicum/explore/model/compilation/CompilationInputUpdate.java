package ru.practicum.explore.model.compilation;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class CompilationInputUpdate {
    private Set<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;
}
