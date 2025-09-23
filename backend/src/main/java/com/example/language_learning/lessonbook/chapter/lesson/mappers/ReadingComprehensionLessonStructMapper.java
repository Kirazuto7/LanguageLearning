package com.example.language_learning.lessonbook.chapter.lesson.mappers;

import com.example.language_learning.lessonbook.chapter.lesson.dtos.ReadingComprehensionLessonDTO;
import com.example.language_learning.lessonbook.chapter.lesson.data.ReadingComprehensionLesson;
import com.example.language_learning.shared.mapper.CycleAvoidingMappingContext;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.QuestionStructMapper;
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
        if (lesson.getLessonQuestions() != null) {
            lesson.getLessonQuestions().forEach(question -> question.setLesson(lesson));
        }
    }
}
