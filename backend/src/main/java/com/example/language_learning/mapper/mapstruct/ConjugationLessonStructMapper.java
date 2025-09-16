package com.example.language_learning.mapper.mapstruct;

import com.example.language_learning.dto.lessons.ConjugationLessonDTO;
import com.example.language_learning.entity.lessons.ConjugationLesson;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {ConjugationExampleStructMapper.class},
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface ConjugationLessonStructMapper {
    @Mapping(target = "type", constant = "CONJUGATION")
    ConjugationLesson toEntity(ConjugationLessonDTO dto);

    ConjugationLessonDTO toDto(ConjugationLesson entity);
}
