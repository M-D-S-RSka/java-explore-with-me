package ru.practicum.explore.model.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryDto {

    private long id;
    @NotBlank
    @Size(max = 50)
    private String name;
}
