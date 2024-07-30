package ru.practicum.explore.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore.utilits.UserEventDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class EventCreateDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    private long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @UserEventDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private boolean paid;
    @PositiveOrZero
    private int participantLimit;
    private Boolean requestModeration;
    @Size(min = 3, max = 120)
    private String title;
}
