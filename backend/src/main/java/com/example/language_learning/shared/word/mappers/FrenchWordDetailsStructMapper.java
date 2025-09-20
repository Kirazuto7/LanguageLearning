package com.example.language_learning.shared.word.mappers;

import com.example.language_learning.shared.word.dtos.FrenchWordDetailsDTO;
import com.example.language_learning.shared.word.data.FrenchWordDetails;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class FrenchWordDetailsStructMapper {
    public abstract FrenchWordDetails toEntity(FrenchWordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract FrenchWordDetailsDTO toDto(FrenchWordDetails entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public FrenchWordDetailsDTO createDto(FrenchWordDetails entity, @Context CycleAvoidingMappingContext context) {
        FrenchWordDetailsDTO existingDto = context.getMappedInstance(entity, FrenchWordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return FrenchWordDetailsDTO.builder().build();
    }
}