package ru.practicum.explore.rest.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.compilation.CompilationOutput;
import ru.practicum.explore.service.compilationService.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationRestHandler {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationOutput> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationOutput getCompilations(@PathVariable Long compId) {
        return compilationService.getCompilation(compId);
    }
}
