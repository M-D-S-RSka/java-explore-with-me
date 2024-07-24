package ru.practicum.explore.model.event;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class Location {
    @Min(-90)
    @Max(90)
    @NotNull
    private Double lat;
    @Min(-180)
    @Max(180)
    @NotNull
    private Double lon;
}
