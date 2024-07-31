package ru.practicum.explore.model.comments;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentOutput {
    private Long id;
    private String userName;
    private String comment;
    private Boolean updated;
    private LocalDateTime lastUpdate;
}
