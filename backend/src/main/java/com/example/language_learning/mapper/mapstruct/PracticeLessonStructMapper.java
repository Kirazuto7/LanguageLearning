package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.entity.lessons.PracticeLesson;
import com.example.language_learning.dto.lessons.PracticeLessonDTO;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {QuestionStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface PracticeLessonStructMapper {
    @Mapping(target = "type", constant = "PRACTICE")
    PracticeLesson toEntity(PracticeLessonDTO dto);

    PracticeLessonDTO toDto(PracticeLesson entity);
}
