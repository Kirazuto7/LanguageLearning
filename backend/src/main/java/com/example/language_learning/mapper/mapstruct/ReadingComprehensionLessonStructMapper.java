package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.lessons.ReadingComprehensionLessonDTO;
import com.example.language_learning.entity.lessons.ReadingComprehensionLesson;
import com.example.language_learning.mapper.CycleAvoidingMappingContext;
import org.mapstruct.AfterMapping;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;


@Mapper(
    componentModel = "spring",
    uses = {QuestionStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public abstract class ReadingComprehensionLessonStructMapper {
    @Mapping(target = "type", constant = "READING_COMPREHENSION")
    @Mapping(target = "page", ignore = true)
    public abstract ReadingComprehensionLesson toEntity(ReadingComprehensionLessonDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract ReadingComprehensionLessonDTO toDto(ReadingComprehensionLesson entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public ReadingComprehensionLessonDTO createDto(ReadingComprehensionLesson entity, @Context CycleAvoidingMappingContext context) {
        ReadingComprehensionLessonDTO existingDto = context.getMappedInstance(entity, ReadingComprehensionLessonDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return ReadingComprehensionLessonDTO.builder().build();
    }

    @AfterMapping
    protected void setLessonOnQuestions(@MappingTarget ReadingComprehensionLesson lesson) {
        if (lesson.getQuestions() != null) {
            lesson.getQuestions().forEach(question -> question.setLesson(lesson));
        }
    }
}
