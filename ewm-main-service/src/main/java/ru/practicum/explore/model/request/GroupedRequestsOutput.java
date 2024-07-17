package ru.practicum.explore.model.request;

import lombok.Data;

import java.util.List;

@Data
public class GroupedRequestsOutput {
    private List<RequestOutput> confirmedRequests;
    private List<RequestOutput> rejectedRequests;
}
