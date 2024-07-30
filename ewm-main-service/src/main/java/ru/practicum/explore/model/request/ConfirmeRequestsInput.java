package ru.practicum.explore.model.request;

import lombok.Data;

import java.util.Set;

@Data
public class ConfirmeRequestsInput {
    private Set<Long> requestIds;
    private RequestStatus status;
}
