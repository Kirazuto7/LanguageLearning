package com.example.language_learning.mappers.mapstruct;

import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.dtos.ConjugationExampleDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.sentence.data.ConjugationExample;
import com.example.language_learning.mappers.CycleAvoidingMappingContext;
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
