package com.example.language_learning.shared.word.mappers;

import com.example.language_learning.shared.word.dtos.KoreanWordDetailsDTO;
import com.example.language_learning.shared.word.data.KoreanWordDetails;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class KoreanWordDetailsStructMapper {
    public abstract KoreanWordDetails toEntity(KoreanWordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract KoreanWordDetailsDTO toDto(KoreanWordDetails entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public KoreanWordDetailsDTO createDto(KoreanWordDetails entity, @Context CycleAvoidingMappingContext context) {
        KoreanWordDetailsDTO existingDto = context.getMappedInstance(entity, KoreanWordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return KoreanWordDetailsDTO.builder().build();
    }
}
