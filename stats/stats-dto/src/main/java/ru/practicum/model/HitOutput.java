package ru.practicum.model;

import lombok.Data;

@Data
public class HitOutput {
    private String app;
    private String uri;
    private Integer hits;
}
