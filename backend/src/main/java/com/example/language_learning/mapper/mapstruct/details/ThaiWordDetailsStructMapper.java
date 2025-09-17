package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.ThaiWordDetailsDTO;
import com.example.language_learning.entity.models.details.ThaiWordDetails;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class ThaiWordDetailsStructMapper {
    public abstract ThaiWordDetails toEntity(ThaiWordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract ThaiWordDetailsDTO toDto(ThaiWordDetails entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public ThaiWordDetailsDTO createDto(ThaiWordDetails entity, @Context CycleAvoidingMappingContext context) {
        ThaiWordDetailsDTO existingDto = context.getMappedInstance(entity, ThaiWordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return ThaiWordDetailsDTO.builder().build();
    }
}