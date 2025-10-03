package com.example.language_learning.lessonbook.chapter.lesson.page.sentence;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring"
)
public abstract class LessonConjugationExampleStructMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract LessonConjugationExample toEntity(LessonConjugationExampleDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract LessonConjugationExampleDTO toDto(LessonConjugationExample entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public LessonConjugationExampleDTO createDto(LessonConjugationExample entity, @Context CycleAvoidingMappingContext context) {
        LessonConjugationExampleDTO existingDto = context.getMappedInstance(entity, LessonConjugationExampleDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return LessonConjugationExampleDTO.builder().build();
    }
}
