package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.dto.user.SettingsDTO;
import com.example.language_learning.entity.user.Settings;
import org.mapstruct.Context;
import org.mapstruct.ObjectFactory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class SettingsStructMapper {
    public abstract Settings toEntity(SettingsDTO dto, @Context CycleAvoidingMappingContext context);
    public abstract SettingsDTO toDto(Settings entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public SettingsDTO createDto(Settings entity, @Context CycleAvoidingMappingContext context) {
        SettingsDTO existingDto = context.getMappedInstance(entity, SettingsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return SettingsDTO.builder().build();
    }
}
