package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.lessons.ReadingComprehensionLessonDTO;
import com.example.language_learning.entity.lessons.ReadingComprehensionLesson;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {QuestionStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface ReadingComprehensionStructMapper {
    @Mapping(target = "type", constant = "READING_COMPREHENSION")
    ReadingComprehensionLesson toEntity(ReadingComprehensionLessonDTO dto);

    ReadingComprehensionLessonDTO toDto(ReadingComprehensionLesson entity);
}
