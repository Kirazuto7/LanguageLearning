package com.example.language_learning.user;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class SettingsStructMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
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
