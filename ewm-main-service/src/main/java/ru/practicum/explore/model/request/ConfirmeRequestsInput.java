package ru.practicum.explore.model.request;

import lombok.Data;

import java.util.List;

@Data
public class ConfirmeRequestsInput {
    private List<Long> requestIds;
    private RequestStatus status;
}
