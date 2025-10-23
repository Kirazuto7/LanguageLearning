package com.example.language_learning.shared.word.mappers;

import com.example.language_learning.shared.word.dtos.SpanishWordDetailsDTO;
import com.example.language_learning.shared.word.data.SpanishWordDetails;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class SpanishWordDetailsStructMapper {
    public abstract SpanishWordDetails toEntity(SpanishWordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract SpanishWordDetailsDTO toDto(SpanishWordDetails entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public SpanishWordDetailsDTO createDto(SpanishWordDetails entity, @Context CycleAvoidingMappingContext context) {
        SpanishWordDetailsDTO existingDto = context.getMappedInstance(entity, SpanishWordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return SpanishWordDetailsDTO.builder().build();
    }
}