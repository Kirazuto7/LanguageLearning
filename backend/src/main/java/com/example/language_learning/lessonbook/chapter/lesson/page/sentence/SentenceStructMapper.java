package com.example.language_learning.lessonbook.chapter.lesson.page.sentence;

import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring"
)
public abstract class SentenceStructMapper {
    public abstract LessonSentence toEntity(LessonSentenceDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract LessonSentenceDTO toDto(LessonSentence entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public LessonSentenceDTO createDto(LessonSentence entity, @Context CycleAvoidingMappingContext context) {
        LessonSentenceDTO existingDto = context.getMappedInstance(entity, LessonSentenceDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return LessonSentenceDTO.builder().build();
    }
}
