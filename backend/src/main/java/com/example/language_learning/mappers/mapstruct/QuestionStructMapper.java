package com.example.language_learning.mappers.mapstruct;

import com.example.language_learning.lessonbook.chapter.lesson.page.question.dtos.QuestionDTO;
import com.example.language_learning.lessonbook.chapter.lesson.page.question.data.Question;
import com.example.language_learning.mappers.CycleAvoidingMappingContext;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(
    componentModel = "spring"
)
public abstract class QuestionStructMapper {
    @Mapping(target="lesson", ignore = true)
    public abstract Question toEntity(QuestionDTO dto, @Context CycleAvoidingMappingContext context);

    public abstract QuestionDTO toDto(Question entity, @Context CycleAvoidingMappingContext context);

    @ObjectFactory
    public QuestionDTO createDto(Question entity, @Context CycleAvoidingMappingContext context) {
        QuestionDTO existingDto = context.getMappedInstance(entity, QuestionDTO.class);
        if (existingDto != null) {
            return existingDto;
        }
        return QuestionDTO.builder().build();
    }
}
