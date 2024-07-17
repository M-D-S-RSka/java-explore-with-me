package ru.practicum.explore.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.explore.utilits.UserEventDate;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class EventUpdateUserDto extends EventUpdateDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @UserEventDate
    private LocalDateTime eventDate;
    private EventStateAction stateAction;
}
