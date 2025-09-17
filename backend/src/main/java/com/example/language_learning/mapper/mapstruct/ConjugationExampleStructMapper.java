package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.models.ConjugationExampleDTO;
import com.example.language_learning.entity.models.ConjugationExample;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring"
)
public abstract class ConjugationExampleStructMapper {
    @Mapping(target = "lesson", ignore = true)
    public abstract ConjugationExample toEntity(ConjugationExampleDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract ConjugationExampleDTO toDto(ConjugationExample entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public ConjugationExampleDTO createDto(ConjugationExample entity, @Context CycleAvoidingMappingContext context) {
        ConjugationExampleDTO existingDto = context.getMappedInstance(entity, ConjugationExampleDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return ConjugationExampleDTO.builder().build();
    }
}
