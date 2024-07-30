package ru.practicum.explore.model.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestOutput {
    private Long id;
    private LocalDateTime created;
    private long event;
    private long requester;
    private RequestStatus status;
}
