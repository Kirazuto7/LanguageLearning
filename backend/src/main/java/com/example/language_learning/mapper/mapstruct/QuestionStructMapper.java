package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.models.QuestionDTO;
import com.example.language_learning.entity.lessons.Lesson;
import com.example.language_learning.entity.models.Question;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring"
)
public interface QuestionStructMapper {
    @Mapping(target = "lesson", source = "lesson")
    Question toEntity(QuestionDTO dto, @Context Lesson lesson);

    QuestionDTO toDto(Question entity);
}
