package com.example.language_learning.shared.word.mappers;

import com.example.language_learning.shared.word.dtos.ChineseWordDetailsDTO;
import com.example.language_learning.shared.word.data.ChineseWordDetails;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class ChineseWordDetailsStructMapper {
    public abstract ChineseWordDetails toEntity(ChineseWordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract ChineseWordDetailsDTO toDto(ChineseWordDetails entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public ChineseWordDetailsDTO createDto(ChineseWordDetails entity, @Context CycleAvoidingMappingContext context) {
        ChineseWordDetailsDTO existingDto = context.getMappedInstance(entity, ChineseWordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return ChineseWordDetailsDTO.builder().build();
    }
}