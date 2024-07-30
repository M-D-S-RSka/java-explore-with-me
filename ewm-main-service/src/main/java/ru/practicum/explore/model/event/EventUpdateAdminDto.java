package ru.practicum.explore.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.explore.utilits.AdminEventDate;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class EventUpdateAdminDto extends EventUpdateDto {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @AdminEventDate
    private LocalDateTime eventDate;
    private EventAdminStateAction stateAction;

}
