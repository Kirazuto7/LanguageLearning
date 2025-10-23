package com.example.language_learning.lessonbook.chapter.lesson.page.question;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring"
)
public abstract class LessonQuestionStructMapper {
    @Mapping(target="lesson", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract LessonQuestion toEntity(LessonQuestionDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract LessonQuestionDTO toDto(LessonQuestion entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public LessonQuestionDTO createDto(LessonQuestion entity, @Context CycleAvoidingMappingContext context) {
        LessonQuestionDTO existingDto = context.getMappedInstance(entity, LessonQuestionDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return LessonQuestionDTO.builder().build();
    }
}
