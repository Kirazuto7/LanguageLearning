package com.example.language_learning.shared.word.mappers;

import com.example.language_learning.shared.word.dtos.GermanWordDetailsDTO;
import com.example.language_learning.shared.word.data.GermanWordDetails;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
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