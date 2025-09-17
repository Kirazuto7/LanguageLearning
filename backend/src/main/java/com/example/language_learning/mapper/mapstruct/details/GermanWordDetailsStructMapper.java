package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.GermanWordDetailsDTO;
import com.example.language_learning.entity.models.details.GermanWordDetails;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class GermanWordDetailsStructMapper {
    public abstract GermanWordDetails toEntity(GermanWordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract GermanWordDetailsDTO toDto(GermanWordDetails entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public GermanWordDetailsDTO createDto(GermanWordDetails entity, @Context CycleAvoidingMappingContext context) {
        GermanWordDetailsDTO existingDto = context.getMappedInstance(entity, GermanWordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return GermanWordDetailsDTO.builder().build();
    }
}