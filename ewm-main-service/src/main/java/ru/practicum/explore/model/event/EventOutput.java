package ru.practicum.explore.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore.model.category.CategoryDto;
import ru.practicum.explore.model.comments.CommentOutput;
import ru.practicum.explore.model.user.UserOutput;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventOutput {
    private long id;
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserOutput initiator;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private String state;
    private String title;
    private int views;
    private List<CommentOutput> comments;
}
