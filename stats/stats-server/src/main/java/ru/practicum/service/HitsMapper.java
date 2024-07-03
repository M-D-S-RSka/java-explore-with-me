package ru.practicum.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.db.model.DbHitData;
import ru.practicum.model.HitInput;

//@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.FIELD)
public interface HitsMapper {
    DbHitData fromInput(HitInput hitInput);
}
