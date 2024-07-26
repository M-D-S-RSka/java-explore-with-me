package ru.practicum.explore.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.model.category.CategoryDto;
import ru.practicum.explore.model.compilation.CompilationInputCreate;
import ru.practicum.explore.model.compilation.CompilationInputUpdate;
import ru.practicum.explore.model.compilation.CompilationOutput;
import ru.practicum.explore.model.event.EventOutput;
import ru.practicum.explore.model.event.EventState;
import ru.practicum.explore.model.event.EventUpdateAdminDto;
import ru.practicum.explore.model.user.UserDto;
import ru.practicum.explore.service.categoryService.CategoryService;
import ru.practicum.explore.service.compilationService.CompilationService;
import ru.practicum.explore.service.eventService.EventService;
import ru.practicum.explore.service.userService.UserService;
import ru.practicum.explore.utilits.Constants;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class AdminRestHandler {
    private final UserService userService;
    private final CategoryService categoryService;
    private final CompilationService compilationService;
    private final EventService eventService;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Constants.PATTERN);

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        userService.deleteUserById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(defaultValue = "") List<Long> ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable long catId) {
        categoryService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable long catId, @RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.updateCategory(catId, categoryDto);
    }

    @GetMapping("/events")
    public Set<EventOutput> getEventsAdmin(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<EventState> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) @Valid @DateTimeFormat(pattern = Constants.PATTERN) LocalDateTime rangeStart,
                                           @RequestParam(required = false) @Valid @DateTimeFormat(pattern = Constants.PATTERN) LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "0") @Valid @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Valid @Positive int size) {
        return eventService.searchEventAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventOutput updateEventAdmin(@PathVariable long eventId, @Valid @RequestBody EventUpdateAdminDto eventUpdateDto) {
        return eventService.updateEventAdmin(eventId, eventUpdateDto);
    }

    @PostMapping("/compilations")
    public ResponseEntity<CompilationOutput> createCompilation(@Valid @RequestBody CompilationInputCreate compilationInputCreate) {
        return new ResponseEntity<>(compilationService.addCompilation(compilationInputCreate), HttpStatus.CREATED);
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<Object> deleteCompilation(@PathVariable long compId) {
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationOutput updateCompilation(@PathVariable long compId, @Valid @RequestBody CompilationInputUpdate compilationInputUpdate) {
        return compilationService.updateCompilation(compId, compilationInputUpdate);
    }
}
