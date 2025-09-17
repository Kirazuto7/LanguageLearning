package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.models.SentenceDTO;
import com.example.language_learning.entity.models.Sentence;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring"
)
public abstract class SentenceStructMapper {
    public abstract Sentence toEntity(SentenceDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract SentenceDTO toDto(Sentence entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public SentenceDTO createDto(Sentence entity, @Context CycleAvoidingMappingContext context) {
        SentenceDTO existingDto = context.getMappedInstance(entity, SentenceDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return SentenceDTO.builder().build();
    }
}
