package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.entity.lessons.PracticeLesson;
import com.example.language_learning.dto.lessons.PracticeLessonDTO;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {QuestionStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class PracticeLessonStructMapper {
    @Mapping(target = "type", constant = "PRACTICE")
    @Mapping(target ="page", ignore = true)
    public abstract PracticeLesson toEntity(PracticeLessonDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract PracticeLessonDTO toDto(PracticeLesson entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public PracticeLessonDTO createDto(PracticeLesson entity, @Context CycleAvoidingMappingContext context) {
        PracticeLessonDTO existingDto = context.getMappedInstance(entity, PracticeLessonDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return PracticeLessonDTO.builder().build();
    }

    @AfterMapping
    protected void setLessonOnQuestions(@MappingTarget PracticeLesson lesson) {
        if (lesson.getQuestions() != null) {
            lesson.getQuestions().forEach(question -> question.setLesson(lesson));
        }
    }
}
