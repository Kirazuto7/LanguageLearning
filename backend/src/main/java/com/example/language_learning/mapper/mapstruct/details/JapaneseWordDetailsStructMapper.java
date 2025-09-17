package com.example.language_learning.mapper.mapstruct.details;

import com.example.language_learning.dto.models.details.JapaneseWordDetailsDTO;
import com.example.language_learning.entity.models.details.JapaneseWordDetails;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class JapaneseWordDetailsStructMapper {
    public abstract JapaneseWordDetails toEntity(JapaneseWordDetailsDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract JapaneseWordDetailsDTO toDto(JapaneseWordDetails entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public JapaneseWordDetailsDTO createDto(JapaneseWordDetails entity, @Context CycleAvoidingMappingContext context) {
        JapaneseWordDetailsDTO existingDto = context.getMappedInstance(entity, JapaneseWordDetailsDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return JapaneseWordDetailsDTO.builder().build();
    }
}
